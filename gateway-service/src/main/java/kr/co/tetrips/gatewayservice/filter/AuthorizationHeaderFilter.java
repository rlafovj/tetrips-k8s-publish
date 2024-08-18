package kr.co.tetrips.gatewayservice.filter;

import java.util.List;

import io.netty.handler.codec.http.cookie.Cookie;
import kr.co.tetrips.gatewayservice.domain.vo.Role;
import kr.co.tetrips.gatewayservice.service.provider.JwtProvider;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>{

  private final JwtProvider jwtTokenProvider;

  public AuthorizationHeaderFilter(JwtProvider jwtProvider){
    super(Config.class);
    this.jwtTokenProvider = jwtProvider;
  }

  @Data
  public static class Config {
    private String headerName;
    private String headerValue;
    @Setter
    private List<Role> role;

  }

  @Override
  public GatewayFilter apply(Config config) {
    return ((exchange, chain) -> {
      log.info("Request URL: {}", exchange.getRequest().getURI());
      if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "http://www.tetrips.co.kr");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,Authorization");
        exchange.getResponse().getHeaders().add("Access-Control-Expose-Headers", "Content-Length,Content-Range");
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        return exchange.getResponse().setComplete();
      }
      if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION))
        return onError(exchange, HttpStatus.UNAUTHORIZED, "No Authorization Header");

//      Cookie[] cookies = exchange.getRequest().getCookies().values().toArray(new Cookie[0]);
//      String accessToken = null;
//      if("accessToken".equals(cookies[0].name()))
//        accessToken = cookies[0].value(); // 쿠키를 까서 엑세스토큰을 추출하는 과정
      @SuppressWarnings("null")
      String token = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

      if(token == null || token.isEmpty())
        return onError(exchange, HttpStatus.UNAUTHORIZED, "No Token");

      String jwt = jwtTokenProvider.removeBearer(token);

      if(!jwtTokenProvider.isTokenValid(jwt, false))
        return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid Token");

      List<Role> role = jwtTokenProvider.extractRoles(jwt);

      for(var i : config.getRole()){
        if(role.contains(i))
          return chain.filter(exchange);
      }

      return onError(exchange, HttpStatus.FORBIDDEN, "No Permission");
    });
  }

  private Mono<Void> onError(ServerWebExchange exchange, HttpStatusCode httpStatusCode, String message){
    log.error("Error Occured : {}, {}, {}", exchange.getRequest().getURI(), httpStatusCode, message);
    exchange.getResponse().setStatusCode(httpStatusCode);
    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(message.getBytes());
    return exchange.getResponse().writeWith(Mono.just(buffer));
  }
}