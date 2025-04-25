package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddBacklogDto(@JsonProperty("name") String name) {
}
