FROM openjdk:17-jdk-slim
VOLUME /tmp
LABEL authors="tetrips"
COPY build/libs/*.jar gateway.jar
ENTRYPOINT ["java","-jar","/gateway.jar"]