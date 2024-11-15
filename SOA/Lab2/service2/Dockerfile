# Используем официальный образ Payara Server
FROM payara/server-full:6.2024.1-jdk17

# Указываем рабочую директорию в контейнере
WORKDIR /opt/payara/deployments

# Копируем WAR файл в директорию для развертывания
COPY build/libs/service2-1.0-SNAPSHOT.war /opt/payara/deployments/

# Payara запускается на порту 8080, это стандартно
EXPOSE 8080