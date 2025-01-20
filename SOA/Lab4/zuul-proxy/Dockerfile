FROM gradle:6.9-jdk11 as builder

WORKDIR /app

COPY . .
RUN gradle bootJar

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
# COPY --from=builder /app/src/main/resources/service2.* /certs/

EXPOSE 8444

ENTRYPOINT ["java", "-jar", "app.jar"]
