package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddMembersDto(
        @JsonProperty("username")
        @NotBlank(message = "Username can't be empty")
        @Size(max = 20)
        String username
) {
}
