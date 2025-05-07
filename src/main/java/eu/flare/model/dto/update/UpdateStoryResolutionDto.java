package eu.flare.model.dto.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateStoryResolutionDto(
        @JsonProperty("name")
        @NotBlank
        @Size(max = 15)
        String name
) {
}
