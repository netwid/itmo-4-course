package dev.controller;

import dev.dto.Percentage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemographyController {
    @GetMapping("/demography/hair-color/{color}/percentage")
    public Percentage hairColor(@PathVariable("color") String color) {
        var response = new Percentage();
        response.setValue(21.12112);
        return response;
    }

    @GetMapping("/demography/eye-color/{color}/percentage")
    public Percentage eyeColor(@PathVariable("color") String color) {
        var response = new Percentage();
        response.setValue(21.12112);
        return response;
    }
}
