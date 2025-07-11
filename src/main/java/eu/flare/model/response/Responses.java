package eu.flare.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.model.*;
import eu.flare.model.dto.response.ResponsesDto;

import java.util.List;

public final class Responses {

    private Responses() {
        //no-op just to prevent instantiation
    }

    public record UserSignedUpResponse(
            @JsonProperty("id") long userId,
            @JsonProperty("username") String username,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("middle_name") String middleName,
            @JsonProperty("last_name") String lastName
    ){}

    public record UserLoggedInResponse(
            @JsonProperty("token") String token,
            @JsonProperty("expiresIn") long expiry
    ) {
    }

    public record SignupRequestValidationErrorResponse(@JsonProperty("error") String reason) {
    }

    public record UsernameExistsErrorResponse(@JsonProperty("error") String reason) {
    }

    public record LoginRequestValidationErrorResponse(@JsonProperty("error") String reason) {
    }

    public record UserNotFoundErrorResponse(@JsonProperty("error") String error) {
    }

    public record CreateProjectErrorResponse(@JsonProperty("error") String errorMessage) { }
    public record CreateProjectResponse(@JsonProperty("project") ResponsesDto.CreateProjectResponseDto project) { }
    public record EpicsNotFoundResponse(@JsonProperty("error") String error){}
    public record ProjectNotFoundResponse(String message) { }
    public record SearchProjectByNameResponse(Project project) { }
    public record ProjectWithEpicsResponse(
            @JsonProperty("project_name") String projectName,
            @JsonProperty("epics") List<Epic> epics
    ) {
    }
    public record ProjectWithEpicsErrorResponse(
            @JsonProperty("error") String error
    ){}
    public record UpdateProjectResponse(
            @JsonProperty("project") Project project
    ){}
    public record SprintNamesConflictsResponse(
            @JsonProperty("error") String error
    ){}
    public record ProjectNameConflictResponse(
            @JsonProperty("error") String error
    ) {}
    public record BacklogAlreadyExistsResponse(
            @JsonProperty("error") String error
    ) {}

    public record EpicNotFoundResponse(@JsonProperty("error") String error) {}
    public record RequestBodyEmptyResponse(@JsonProperty("error") String error) {}
    public record UserNotFoundException(@JsonProperty("error") String error) {}
    public record StoryCreationException(@JsonProperty("error") String error) {}

    public record StoryCreatedResponse(@JsonProperty("epic") Epic epic) {}
    public record EpicResponse(@JsonProperty("epic") Epic epic) {}

    public record BacklogResponse(@JsonProperty("backlog") Backlog backlog) {}
    public record BacklogNotFoundResponse(@JsonProperty("error") String error) {}

    public record TaskNotFoundResponse(@JsonProperty("error") String error){}
    public record TaskResponse(@JsonProperty("task") Task task) {}
    public record TaskUpdatedResponse(@JsonProperty("task") Task task) {}

    public record StoryResponse(@JsonProperty("story") Story story){}
    public record TaskNamesConflictResponse(@JsonProperty("error") String error) {}
    public record StoryNotFoundResponse(@JsonProperty("error") String error){}
    public record StoryUpdatedResponse(@JsonProperty("story") Story story) {}

    public record RefreshTokenNotFoundResponse(@JsonProperty("error") String error) {}
    public record UserLoggedOutSuccessfullyResponse(@JsonProperty("message") String message) {}

    public record UnknownOperationResponse(@JsonProperty("error") String error) {}
    public record SprintNotFoundResponse(@JsonProperty("error") String error) {}
    public record SprintResponse(@JsonProperty("sprint") Sprint sprint) {}
    public record SprintAlreadyStartedResponse(@JsonProperty("error") String error) {}
    public record SprintNotStartedResponse(@JsonProperty("error") String error) {}
    public record SprintAlreadyCompletedResponse(@JsonProperty("error") String error) {}

    public record BoardNotFoundResponse(@JsonProperty("error") String error) {}
    public record BoardResponse(@JsonProperty("board") Board board) {}
    public record BoardCreationConflictResponse(@JsonProperty("error") String error) {}
}
