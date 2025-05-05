package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record AddBoardDto(
        @JsonProperty("name")
        @NotEmpty
        @Size(max = 150)
        String name
) {
}
