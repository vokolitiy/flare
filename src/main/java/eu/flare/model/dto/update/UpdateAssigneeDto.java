package eu.flare.model.dto.update;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateAssigneeDto(@JsonProperty("name") String name){
}
