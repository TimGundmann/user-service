FROM openjdk:8-jre-alpine

COPY target/eureka-service.jar /app/eureka-service.jar

CMD ["java", "-jar", "/app/eureka-service.jar"]
