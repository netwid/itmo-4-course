package dev.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "personsResponse")
@XmlAccessorType(XmlAccessType.NONE)
@Setter
@Getter
public class PersonsResponseDTO {
    private int totalResults;
}
