package dev.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@JacksonXmlRootElement(localName = "person")
public class PersonDTO {
    private Integer id;
    private String name;
}

