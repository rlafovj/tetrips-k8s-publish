FROM openjdk:17-jdk-slim
VOLUME /tmp
LABEL authors="tetrips"
COPY build/libs/*.jar user.jar
ENTRYPOINT ["java","-jar","/user.jar"]