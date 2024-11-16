FROM gradle:8.8-jdk17 as builder

WORKDIR /app

COPY . .
RUN gradle clean build

FROM payara/server-full:6.2024.1-jdk17

COPY keystore.jks /opt/payara/appserver/glassfish/domains/domain1/config/

COPY asadmin-credentials /opt/payara/
COPY .asadminpass /opt/payara/

RUN asadmin start-domain \
    && asadmin --user admin --passwordfile=/opt/payara/asadmin-credentials set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-2.enabled=true \
    && asadmin --user admin --passwordfile=/opt/payara/asadmin-credentials set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.enabled=false \
    && asadmin --user admin --passwordfile=/opt/payara/asadmin-credentials create-network-listener --protocol http-listener-2 --listenerport 8444 --jkenabled false --target server-config https-listener-2 \
    && asadmin --user admin --passwordfile=/opt/payara/asadmin-credentials set configs.config.server-config.network-config.protocols.protocol.http-listener-2.ssl.key-store=/opt/payara/appserver/glassfish/domains/domain1/config/keystore.jks \
    && asadmin --user admin --passwordfile=/opt/payara/asadmin-credentials set configs.config.server-config.network-config.protocols.protocol.http-listener-2.ssl.key-store-password=changeme \
    && asadmin --user admin --passwordfile=/opt/payara/asadmin-credentials set configs.config.server-config.network-config.protocols.protocol.http-listener-2.ssl.trust-store=/opt/payara/appserver/glassfish/domains/domain1/config/keystore.jks \
    && asadmin --user admin --passwordfile=/opt/payara/asadmin-credentials set configs.config.server-config.network-config.protocols.protocol.http-listener-2.ssl.trust-store-password=changeme \
    && asadmin --user admin --passwordfile=/opt/payara/asadmin-credentials set configs.config.server-config.network-config.protocols.protocol.http-listener-2.ssl.cert-nickname=tomcat \
    && asadmin stop-domain

# Указываем рабочую директорию в контейнере
WORKDIR /opt/payara/deployments

# Копируем WAR файл в директорию для развертывания
COPY --from=builder /app/build/libs/*.war /opt/payara/deployments/

# Payara запускается на порту 8080, это стандартно
EXPOSE 8080