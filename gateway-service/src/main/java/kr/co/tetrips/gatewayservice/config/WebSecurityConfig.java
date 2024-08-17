package kr.co.tetrips.gatewayservice.config;

import kr.co.tetrips.gatewayservice.handler.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
  private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
  private final ReactiveClientRegistrationRepository reactiveClientRegistrationRepository;

  @Bean
  public ServerOAuth2AuthorizationRequestResolver serverOAuth2AuthorizationRequestResolver() {
    return new DefaultServerOAuth2AuthorizationRequestResolver(reactiveClientRegistrationRepository);
  }


  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
            .authorizeExchange(authorize -> authorize
                    .pathMatchers(
                            ("/login/oauth2/code/**"),
                            ("/user/login/**"),
                            ("/user/signup/**"),
                            ("/error")
                    ).permitAll()
                    .anyExchange().permitAll()//.authenticated()
            )
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .httpBasic(i -> i.disable())
            .csrf(i -> i.disable())
            .cors(cors -> cors.configurationSource(configureCors()))
            .formLogin(i -> i.disable())
            .oauth2Login(oauth -> oauth
                    .authorizationRequestResolver(serverOAuth2AuthorizationRequestResolver())
                    .authenticationSuccessHandler(customAuthenticationSuccessHandler)
            )

            .build();
  }
  @Bean
  public CorsConfigurationSource configureCors() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(List.of("http://www.tetrips.co.kr", "https://www.tetrips.co.kr"));    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}