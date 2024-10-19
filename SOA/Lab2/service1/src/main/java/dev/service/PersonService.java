package dev.service;

import dev.dto.PersonDTO;
import dev.entity.Person;
import dev.repository.PersonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<PersonDTO> getPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream().map(p -> modelMapper.map(p, PersonDTO.class)).collect(Collectors.toList());
    }

    public PersonDTO savePerson(PersonDTO personDTO) {
        Person person = modelMapper.map(personDTO, Person.class);
        return modelMapper.map(personRepository.save(person), PersonDTO.class);
    }
}
