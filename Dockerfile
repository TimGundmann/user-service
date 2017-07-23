FROM openjdk:8-jre-alpine

COPY target/user-service.jar /app/user-service.jar

CMD ["java", "-jar", "/app/user-service.jar"]
