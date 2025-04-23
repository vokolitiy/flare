package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RenameEpicDto(@JsonProperty("name") String name) {
}
