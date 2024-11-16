FROM gradle:8.8-jdk17 as builder

WORKDIR /app

COPY . .
RUN gradle clean build

FROM payara/server-full:6.2024.1-jdk17

# Указываем рабочую директорию в контейнере
WORKDIR /opt/payara/deployments

# Копируем WAR файл в директорию для развертывания
COPY --from=builder /app/build/libs/*.war /opt/payara/deployments/

# Payara запускается на порту 8080, это стандартно
EXPOSE 8080