package eu.flare.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.model.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AddTaskDto(
        @JsonProperty("name") @NotBlank String name,
        @JsonProperty("description") @NotBlank String description,
        @JsonProperty("completion_date") @NotBlank long estimatedCompletionDate,
        @JsonProperty("original_estimate") @NotBlank long originalEstimate,
        @JsonProperty("remaining_estimate") @NotBlank long remainingEstimate,
        @JsonProperty("story_points") @NotBlank int storyPoints,
        @JsonProperty("task_priority") @NotBlank TaskPriority taskPriority,
        @JsonProperty("task_progress") @NotBlank TaskProgress taskProgress,
        @JsonProperty("task_creator") @NotBlank User taskCreator,
        @JsonProperty("task_assignee") @NotBlank  User taskAssignee
){
}
