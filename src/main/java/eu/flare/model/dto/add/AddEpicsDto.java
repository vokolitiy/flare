package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record AddEpicsDto(
        @JsonProperty("epics")
        @NotEmpty
        @Valid
        List<@Pattern(regexp = "^[A-Za-z.\\s_-]+$") String> epicNames
) {
}
