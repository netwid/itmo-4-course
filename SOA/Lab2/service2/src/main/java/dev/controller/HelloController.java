package dev.controller;

import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class HelloController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello dgfhjkl";
    }

    @GetMapping("/demography")
    public String check() {
        String url = "https://172.20.0.2:8443/persons/search";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        // Выполняем POST-запрос
        HttpEntity<String> entity = new HttpEntity<>("<?xml version=\"1.0\" encoding=\"UTF-8\"?><searchRequest></searchRequest>", headers);

        // Возвращаем ответ
        return restTemplate.postForObject(url, entity, String.class);
    }
}