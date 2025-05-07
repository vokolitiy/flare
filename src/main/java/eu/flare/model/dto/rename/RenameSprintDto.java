package eu.flare.model.dto.rename;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RenameSprintDto(
        @JsonProperty("name")
        @NotEmpty
        @Size(max = 30)
        String name
) {
}
