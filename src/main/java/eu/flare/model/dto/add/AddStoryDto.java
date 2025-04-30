package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AddStoryDto(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("completion_date") long estimatedCompletionDate,
        @JsonProperty("original_estimate") long originalEstimate,
        @JsonProperty("remaining_estimate") long remainingEstimate,
        @JsonProperty("story_points") int storyPoints,
        @JsonProperty("story_priority") String storyPriorityName,
        @JsonProperty("story_progress") String storyProgressName,
        @JsonProperty("story_creator") String storyCreatorName,
        @JsonProperty("story_assignee") String storyAssigneeName,
        @JsonProperty("story_watchers") List<String> storyWatcherNames
        ){
}
