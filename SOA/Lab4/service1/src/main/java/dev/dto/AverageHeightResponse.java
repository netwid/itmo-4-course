package dev.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JacksonXmlRootElement(localName = "averageHeight")
public class AverageHeightResponse {

    @JacksonXmlProperty(localName = "value")
    private Double value;

    public AverageHeightResponse(Double value) {
        this.value = value;
    }

    public AverageHeightResponse() {}
}
