package eu.flare.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateProjectErrorResponse(@JsonProperty("error") String errorMessage) {
}
