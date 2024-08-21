FROM openjdk:21-jdk-slim
LABEL authors="thery"
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]