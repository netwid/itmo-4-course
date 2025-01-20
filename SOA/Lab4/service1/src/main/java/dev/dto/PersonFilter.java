package dev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class PersonFilter {
    private List<String> name;
    private HeightFilter height;
    private WeightFilter weight;
    private List<String> passportID;
    private List<String> nationality;
    private List<String> eyeColor;
    private List<String> hairColor;
    private CoordinatesFilter coordinates;
    private LocationFilter location;
    private CreationDateFilter creationDate;

    @Getter
    @Setter
    public static class HeightFilter {
        private Float min;
        private Float max;
        private List<Float> in;
    }

    @Getter
    @Setter
    public static class WeightFilter {
        private Integer min;
        private Integer max;
        private List<Integer> in;
    }

    @Getter
    @Setter
    public static class CoordinatesFilter {
        private List<CoordinatesDTO> in;
    }

    @Getter
    @Setter
    public static class LocationFilter {
        private List<LocationDTO> in;
    }

    @Getter
    @Setter
    public static class CreationDateFilter {
        private Date min;
        private Date max;
        private List<Date> in;
    }
}
