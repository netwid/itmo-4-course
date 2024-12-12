package dev;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;

import java.io.File;
import java.io.IOException;


public class App {
    private static final int PORT = getPort();
    private static final String KEYSTORE_PATH = "service1.jks";
    private static final String KEYSTORE_PASSWORD = "changeit";
    private static final String KEY_ALIAS = "service1";

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(createTempDir());
        tomcat.setPort(PORT);

        // Настраиваем SSL Connector
        Connector httpsConnector = createSslConnector();
        tomcat.getService().addConnector(httpsConnector);
        tomcat.setConnector(httpsConnector);

        tomcat.getHost().setAppBase(".");
        tomcat.addWebapp("", ".");

        tomcat.start();
        tomcat.getServer().await();
    }

    private static Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(PORT);
        connector.setSecure(true);
        connector.setScheme("https");

        connector.setProperty("SSLEnabled", "true");

        // Создаем SSLHostConfig
        SSLHostConfig sslHostConfig = new SSLHostConfig();
        sslHostConfig.setHostName("_default_");
        sslHostConfig.setProtocols("TLSv1.2,TLSv1.3");

        // Создаем SSLHostConfigCertificate и настраиваем его
        SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.RSA);
        certificate.setCertificateKeystoreFile(KEYSTORE_PATH);
        certificate.setCertificateKeystorePassword(KEYSTORE_PASSWORD);
        certificate.setCertificateKeyAlias(KEY_ALIAS);

        // Добавляем сертификат к SSLHostConfig
        sslHostConfig.addCertificate(certificate);

        // Добавляем SSLHostConfig к коннектору
        connector.addSslHostConfig(sslHostConfig);

        return connector;
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        if (port != null) {
            return Integer.parseInt(port);
        }
        return 8443;
    }

    private static String createTempDir() {
        try {
            File tempDir = File.createTempFile("tomcat.", "." + PORT);
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            return tempDir.getAbsolutePath();
        } catch (IOException ex) {
            throw new RuntimeException(
                    "Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"), ex
            );
        }
    }
}