package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddBacklogDto(
        @JsonProperty("name")
        @NotBlank(message = "Backlog name can't be empty")
        @Size(max = 30)
        String name
) {
}
