package eu.flare.model.dto.rename;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameStoryDto(
        @JsonProperty("name")
        @NotBlank
        @Size(max = 150)
        String name
) {
}
