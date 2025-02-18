package dev.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dev.dto.Percentage;
import dev.dto.PersonsResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import se.ifmo.ru.service2.demography.EyeColorRequest;
import se.ifmo.ru.service2.demography.HairColorRequest;
import se.ifmo.ru.service2.demography.PercentageResponse;

@Endpoint
public class DemographyController {
    @Value("{query.url}")
    String url;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String NAMESPACE_URI = "http://se/ifmo/ru/service2/demography";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "hairColorRequest")
    @ResponsePayload
    public PercentageResponse hairColor(@RequestPayload HairColorRequest request) {
        var response = new PercentageResponse();

        int total = query("");
        if (total == 0) {
            response.setPercentage(0);
            return response;
        }

        int hairTotal = query("<personFilter><hairColor><value>" + request.getHairColor() + "</value></hairColor></personFilter>");

        response.setPercentage(100 * hairTotal / total);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "eyeColorRequest")
    @ResponsePayload
    public PercentageResponse eyeColor(@RequestPayload EyeColorRequest request) {
        var response = new PercentageResponse();

        int total = query("");
        if (total == 0) {
            response.setPercentage(0);
            return response;
        }

        int eyeTotal = query("<personFilter><eyeColor><value>" + request.getEyeColor() + "</value></eyeColor></personFilter>");

        response.setPercentage(100 * eyeTotal / total);
        return response;
    }

    private int query(String query) {
        String url = "http://172.20.0.2:8443/persons/search";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        HttpEntity<String> entity = new HttpEntity<>("<?xml version=\"1.0\" encoding=\"UTF-8\"?><searchRequest>" +
                query + "<page>1</page><size>1</size></searchRequest>", headers);

        String response = restTemplate.postForObject(url, entity, String.class);

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        PersonsResponseDTO person;
        try {
            person = xmlMapper.readValue(response, PersonsResponseDTO.class);
        } catch (JsonProcessingException e) {
            return 0;
        }

        return person.getTotalResults();
    }
}
