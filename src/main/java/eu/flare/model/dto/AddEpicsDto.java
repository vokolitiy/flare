package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.model.Epic;

import java.util.List;

public record AddEpicsDto(
        @JsonProperty("epics") List<Epic> epics
) {
}
