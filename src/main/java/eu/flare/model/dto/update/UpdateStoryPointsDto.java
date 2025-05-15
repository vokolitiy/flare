package eu.flare.model.dto.update;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateStoryPointsDto(@JsonProperty("story_points") int storyPoints) {
}
