package dev.controller;

import dev.dto.*;
import dev.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    PersonService service;

    @PostMapping
    public ResponseEntity<PersonDTO> savePerson(@RequestBody PersonDTO personDTO) {
        PersonDTO savedPerson = service.savePerson(personDTO);
        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }

    @PostMapping("/search")
    public ResponseEntity<PersonsResponse> searchPersons(@RequestBody SearchRequest searchRequest) {
        System.out.println(searchRequest);
        PersonsResponse response = service.searchPersons(searchRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable("id") Long id) {
        PersonDTO person = service.getPersonById(id);
        return person != null ? ResponseEntity.ok(person) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable("id") Long id, @RequestBody PersonDTO personDTO) {
        PersonDTO updatedPerson = service.updatePerson(id, personDTO);
        return updatedPerson != null ? ResponseEntity.ok(updatedPerson) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable("id") Long id) {
        boolean deleted = service.deletePerson(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/average-height")
    public ResponseEntity<AverageHeightResponse> getAverageHeight() {
        Double averageHeight = service.getAverageHeight();
        AverageHeightResponse response = new AverageHeightResponse(averageHeight);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count-by-location")
    public ResponseEntity<CountResponse> countByLocation(
            @RequestParam("x") Integer x,
            @RequestParam("y") float y,
            @RequestParam("z") float z,
            @RequestParam("name") String name) {
        Long count = service.countByLocation(x, y, z, name);
        CountResponse response = new CountResponse(count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nationality-greater-than")
    public ResponseEntity<PersonsResponse> getPersonsByNationalityGreaterThan(
            @RequestParam("nationality") String nationality,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        List<PersonDTO> persons = service.getPersonsByNationalityGreaterThan(nationality, page, size);
        int totalPages = persons.size() > 0 ? (int) Math.ceil((double) persons.size() / size) : 0;

        PersonsResponse response = new PersonsResponse(persons.size(), totalPages, page, size, persons);
        return ResponseEntity.ok(response);
    }

}
