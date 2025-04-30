package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AddEpicsDto(
        @JsonProperty("epics") List<String> epicNames
) {
}
