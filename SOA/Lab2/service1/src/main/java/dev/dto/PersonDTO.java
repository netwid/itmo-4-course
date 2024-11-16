package dev.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@Getter
@Setter
@JacksonXmlRootElement(localName = "person")
public class PersonDTO {

    private Long id;

    private String name;

    private CoordinatesDTO coordinates;

    private Date creationDate;

    private Float height;

    private Integer weight;

    private String passportID;

    private String nationality;

    private LocationDTO location;

    private String eyeColor;

    private String hairColor;
}
