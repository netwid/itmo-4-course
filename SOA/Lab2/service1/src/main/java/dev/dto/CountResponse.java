package dev.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JacksonXmlRootElement(localName = "count")
public class CountResponse {

    @JacksonXmlProperty(localName = "value")
    private Long value;

    public CountResponse(Long value) {
        this.value = value;
    }

    public CountResponse() {}
}
