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

@RestController
public class DemographyController {
    @Value("{query.url}")
    String url;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/demography/hair-color/{color}/percentage")
    public Percentage hairColor(@PathVariable("color") String color) {
        var response = new Percentage();

        int total = query("");
        if (total == 0) {
            response.setValue(0);
            return response;
        }

        int hairTotal = query("<personFilter><hairColor><value>" + color + "</value></hairColor></personFilter>");

        response.setValue(100 * hairTotal / total);
        return response;
    }

    @GetMapping("/demography/eye-color/{color}/percentage")
    public Percentage eyeColor(@PathVariable("color") String color) {
        var response = new Percentage();

        int total = query("");
        if (total == 0) {
            response.setValue(0);
            return response;
        }

        int eyeTotal = query("<personFilter><eyeColor><value>" + color + "</value></eyeColor></personFilter>");

        response.setValue(100 * eyeTotal / total);
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

        PersonsResponseDTO person = null;
        try {
            person = xmlMapper.readValue(response, PersonsResponseDTO.class);
        } catch (JsonProcessingException e) {
            return 0;
        }

        return person.getTotalResults();
    }
}
