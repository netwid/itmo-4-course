FROM gradle:8.8-jdk17 as builder

WORKDIR /app

COPY . .
RUN gradle bootJar

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

RUN apt-get update && apt-get install -y curl

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
