FROM openjdk:17-jdk-slim
VOLUME /tmp
LABEL authors="tetrips"
COPY build/libs/*.jar eureka.jar
ENTRYPOINT ["java","-jar","/eureka.jar"]