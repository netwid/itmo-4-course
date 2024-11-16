package dev.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(localName = "personsResponse")
public class PersonsResponse {

    @JacksonXmlProperty(localName = "totalResults")
    private long totalResults;

    @JacksonXmlProperty(localName = "totalPages")
    private int totalPages;

    @JacksonXmlProperty(localName = "currentPage")
    private int currentPage;

    @JacksonXmlProperty(localName = "pageSize")
    private int pageSize;

    @JacksonXmlElementWrapper(localName = "persons")
    @JacksonXmlProperty(localName = "person")
    private List<PersonDTO> persons;

    public PersonsResponse() {}

    public PersonsResponse(long totalResults, int totalPages, int currentPage, int pageSize, List<PersonDTO> persons) {
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.persons = persons;
    }
}
