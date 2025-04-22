package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddMembersDto(@JsonProperty("username") String username) {
}
