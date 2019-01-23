FROM openjdk:10.0.2-13-jre-sid

COPY target/user-service.jar /app/user-service.jar

CMD ["java", "-jar", "/app/user-service.jar"]
