package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddSprintDto(
        @JsonProperty("name") String name
) {
}
