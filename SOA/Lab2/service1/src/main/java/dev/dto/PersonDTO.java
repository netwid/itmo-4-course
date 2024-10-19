package dev.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PersonDTO {
    private Integer id;
    private String name;
}
