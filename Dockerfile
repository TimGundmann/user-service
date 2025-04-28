FROM eclipse-temurin:23-jdk-alpine

COPY target/user-service.jar /app/user-service.jar

CMD ["java", "-jar", "/app/user-service.jar", "--spring.profiles.active=prod"]
