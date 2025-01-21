package dev.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import se.ifmo.ru.service2.demography.HelloResponse;

@Endpoint
@RequiredArgsConstructor
public class HelloController {
    private static final String NAMESPACE_URI = "http://se/ifmo/ru/service2/demography";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "helloRequest")
    @ResponsePayload
    public HelloResponse sayHello() {
        HelloResponse response = new HelloResponse();
        response.setString("Hello");
        return response;
    }
}