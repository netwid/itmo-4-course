//package dev.config;
//
//import org.springframework.cloud.config.client.ConfigClientProperties;
//import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//@Configuration
//public class ConfigClientConfiguration {
//
//    @Bean
//    public ConfigClientProperties configClientProperties(Environment environment) {
//        ConfigClientProperties properties = new ConfigClientProperties(environment);
//        properties.setName("service2");
//        properties.setUri(new String[]{"http://172.20.0.25:8888"});
//        System.out.println("ConfigClientProperties initialized with name: " + properties.getName());
//        return properties;
//    }
//
//    @Bean
//    public ConfigServicePropertySourceLocator configServicePropertySourceLocator(ConfigClientProperties configClientProperties) {
//        System.out.println("Initializing ConfigServicePropertySourceLocator with URI: " + String.join(", ", configClientProperties.getUri()));
//        return new ConfigServicePropertySourceLocator(configClientProperties);
//    }
//}
