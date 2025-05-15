package eu.flare.model.dto.update;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateEstimateDto(
        @JsonProperty("updated_estimate_millis") long millis
) {
}
