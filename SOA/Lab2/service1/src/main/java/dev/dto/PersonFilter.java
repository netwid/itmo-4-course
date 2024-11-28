package dev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PersonFilter {
    private List<String> name;
    private HeightFilter height;
    private List<String> nationality;
    private CoordinatesFilter coordinates;
    private LocationFilter location;
    // Дополнительные поля можно добавить аналогично

    @Getter
    @Setter
    public static class HeightFilter {
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
}
