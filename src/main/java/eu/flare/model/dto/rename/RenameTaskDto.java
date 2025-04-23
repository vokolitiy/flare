package eu.flare.model.dto.rename;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RenameTaskDto(@JsonProperty("name") String name) {
}
