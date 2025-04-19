package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        @JsonProperty("token") String token,
        @JsonProperty("expiresIn") long expiry
) {
}
