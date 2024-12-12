FROM gradle:8.8-jdk17 as builder

WORKDIR /app

#COPY ../build.gradle ./
COPY . .
RUN gradle shadowJar

#RUN gradle clean build -x test --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]
