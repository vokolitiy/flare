package eu.flare.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.model.Project;

public record CreateProjectResponse(@JsonProperty("project") Project project) {
}
