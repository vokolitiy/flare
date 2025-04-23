package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddMembersDto(@JsonProperty("username") String username) {
}
