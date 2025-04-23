package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RenameStoryDto(@JsonProperty("name") String name) {
}
