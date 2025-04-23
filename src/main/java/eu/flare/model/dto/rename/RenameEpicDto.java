package eu.flare.model.dto.rename;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RenameEpicDto(@JsonProperty("name") String name) {
}
