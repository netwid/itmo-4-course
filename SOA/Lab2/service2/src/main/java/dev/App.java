package dev;

import dev.config.SpringConfig;
import fish.payara.micro.PayaraMicro;
import fish.payara.micro.PayaraMicroRuntime;
import jakarta.servlet.*;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.eclipse.jetty.server.Server;
import org.glassfish.embeddable.archive.ScatteredArchive;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    public static void main(String[] args) {
        try {
            // Инициализация Payara Embedded
            GlassFishRuntime runtime = GlassFishRuntime.bootstrap();
            GlassFishProperties glassfishProperties = new GlassFishProperties();
            glassfishProperties.setPort("http-listener", 9080);  // Установка порта HTTP
            glassfishProperties.setPort("https-listener", 9845); // Установка порта HTTPS

            // Запуск Payara Embedded
            GlassFish glassfish = runtime.newGlassFish(glassfishProperties);
            glassfish.start();

            // Инициализация Spring MVC
            initializeSpringMvcContext();

            System.out.println("Payara Embedded started on port 9080");

        } catch (GlassFishException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void initializeSpringMvcContext() {
        // Создаем контекст Spring
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.scan("com.example");  // Укажите свой пакет с бинами
        context.refresh();

        // Создаем встроенный контейнер для сервлетов и регистрируем DispatcherServlet
        try {
            EmbeddedServletContainer container = new EmbeddedServletContainer(9080);
            container.addServlet("dispatcher", new DispatcherServlet(context), "/");
            container.start();
        } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error initializing Spring MVC", e);
        }
    }
}

class EmbeddedServletContainer {

    private final int port;
    private final Server server;

    public EmbeddedServletContainer(int port) {
        this.port = port;
        this.server = new Server(port);
    }

    public void addServlet(String name, Servlet servlet, String mapping) {
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");
        server.setHandler(contextHandler);

        ServletRegistration.Dynamic servletReg = contextHandler.addServlet(name, servlet);
        servletReg.addMapping(mapping);
    }

    public void start() throws Exception {
        server.start();
        System.out.println("Embedded container started on port " + port);
    }

    public void stop() throws Exception {
        server.stop();
    }
}