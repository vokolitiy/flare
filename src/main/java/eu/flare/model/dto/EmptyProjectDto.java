package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmptyProjectDto(
        @JsonProperty
        @NotBlank(message = "Project can't be empty")
        @Size(max = 30)
        String name) {
}
