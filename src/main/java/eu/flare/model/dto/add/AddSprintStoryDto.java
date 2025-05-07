package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddSprintStoryDto(@JsonProperty("story_id") long storyId) {
}
