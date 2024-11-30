package dev.dto;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "personsResponse")
@Setter
@Getter
public class PersonsResponseDTO {
    private int totalResults;
}
