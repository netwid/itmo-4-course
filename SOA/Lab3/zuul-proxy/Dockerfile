FROM gradle:8.8-jdk17 as builder

WORKDIR /app

COPY . .
RUN gradle bootJar

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=builder /app/src/main/resources/service2.* /certs/

EXPOSE 8444

ENTRYPOINT ["java", "-Djavax.net.ssl.trustStore=/certs/service2.jks", "-Djavax.net.ssl.trustStorePassword=changeit", "-jar", "app.jar"]