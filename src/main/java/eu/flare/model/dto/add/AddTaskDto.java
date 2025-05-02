package eu.flare.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddTaskDto(
        @JsonProperty("name")
        @NotBlank
        @Size(max = 150)
        String name,
        @JsonProperty("description")
        @NotBlank
        String description,
        @JsonProperty("completion_date")
        long estimatedCompletionDate,
        @JsonProperty("original_estimate")
        long originalEstimate,
        @JsonProperty("remaining_estimate")
        long remainingEstimate,
        @JsonProperty("story_points")
        int storyPoints,
        @JsonProperty("task_priority")
        @NotBlank
        @Size(max = 15)
        String taskPriority,
        @JsonProperty("task_progress")
        @NotBlank
        @Size(max = 15)
        String taskProgress,
        @JsonProperty("task_creator")
        @NotBlank
        @Size(max = 20)
        String taskCreator,
        @JsonProperty("task_assignee")
        @NotBlank
        @Size(max = 20)
        String taskAssignee
){
}
