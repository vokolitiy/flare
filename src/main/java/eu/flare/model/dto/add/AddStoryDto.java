package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.model.StoryPriority;
import eu.flare.model.StoryProgress;
import eu.flare.model.User;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AddStoryDto(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("completion_date") long estimatedCompletionDate,
        @JsonProperty("original_estimate") long originalEstimate,
        @JsonProperty("remaining_estimate") long remainingEstimate,
        @JsonProperty("story_points") int storyPoints,
        @JsonProperty("story_priority") StoryPriority storyPriority,
        @JsonProperty("story_progress") StoryProgress storyProgress,
        @JsonProperty("story_creator") User storyCreator,
        @JsonProperty("story_assignee") User storyAssignee,
        @JsonProperty("story_watchers") List<User> storyWatchers
        ){
}
