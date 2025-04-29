package eu.flare.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Responses {

    private Responses() {
        //no-op just to prevent instantiation
    }

    public record UserSignedUpResponse(
            @JsonProperty("id") long userId,
            @JsonProperty("username") String username,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("middle_name") String middleName,
            @JsonProperty("last_name") String lastName
    ){}
}
