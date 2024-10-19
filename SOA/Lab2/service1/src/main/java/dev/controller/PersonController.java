package dev.controller;

import dev.dto.PersonDTO;
import dev.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class PersonController {
    @Autowired
    PersonService service;

    @GetMapping("/findPersons")
    public List<PersonDTO> findPersons() {
        return service.getPersons();
    }

    @PostMapping("/savePerson")
    public PersonDTO savePerson(@RequestBody PersonDTO personDTO) {
        return service.savePerson(personDTO);
    }
}
