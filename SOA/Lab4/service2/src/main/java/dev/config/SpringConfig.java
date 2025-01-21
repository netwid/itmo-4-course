package dev.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@Configuration
@ComponentScan("dev")
@EnableWs
public class SpringConfig extends WsConfigurerAdapter {
//    @Bean
//    public MappingJackson2XmlHttpMessageConverter xmlHttpMessageConverter() {
//        return new MappingJackson2XmlHttpMessageConverter();
//    }

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/*");
    }

    @Bean(name = "demography")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema demographySchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("DemographyPort");
        wsdl11Definition.setLocationUri("/");
        wsdl11Definition.setTargetNamespace("http://se/ifmo/ru/service2/demography");
        wsdl11Definition.setSchema(demographySchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema demographySchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/demography.xsd"));
    }
}
