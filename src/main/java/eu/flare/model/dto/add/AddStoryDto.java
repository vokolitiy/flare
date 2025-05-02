package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AddStoryDto(
        @JsonProperty("name")
        @NotBlank
        @Size(max = 150)
        String name,
        @JsonProperty("description")
        @NotBlank
        String description,
        @JsonProperty("completion_date")
        @NotBlank
        long estimatedCompletionDate,
        @JsonProperty("original_estimate")
        long originalEstimate,
        @JsonProperty("remaining_estimate")
        long remainingEstimate,
        @JsonProperty("story_points")
        int storyPoints,
        @JsonProperty("story_priority")
        @NotBlank
        @Size(max = 15)
        String storyPriorityName,
        @JsonProperty("story_progress")
        @NotBlank
        @Size(max = 15)
        String storyProgressName,
        @JsonProperty("story_creator")
        @NotBlank
        @Size(max = 20)
        String storyCreatorName,
        @JsonProperty("story_assignee")
        @NotBlank
        @Size(max = 20)
        String storyAssigneeName,
        @JsonProperty("story_watchers")
        @NotEmpty
        @Valid
        List<@Pattern(regexp = "^[A-Za-z.\\s_-]+$") String> storyWatcherNames
){
}
