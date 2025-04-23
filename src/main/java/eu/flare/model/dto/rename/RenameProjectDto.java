package eu.flare.model.dto.rename;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RenameProjectDto(@JsonProperty("name") String newProjectName) {
}
