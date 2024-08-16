FROM openjdk:17-jdk
WORKDIR /app
COPY target/notification-service-0.0.1-SNAPSHOT.jar /app/notification-service.jar
ENTRYPOINT ["java", "-jar", "notification-service.jar"]