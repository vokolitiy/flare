package eu.flare.controller;

import eu.flare.exceptions.conflicts.TasksNamesConflictException;
import eu.flare.exceptions.misc.UnknownPriorityTypeException;
import eu.flare.exceptions.misc.UnknownProgressTypeException;
import eu.flare.exceptions.notfound.StoryNotFoundException;
import eu.flare.model.Story;
import eu.flare.model.dto.add.AddTaskDto;
import eu.flare.model.dto.rename.RenameStoryDto;
import eu.flare.model.dto.update.*;
import eu.flare.model.response.Responses;
import eu.flare.service.StoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/story")
public class StoryController {

    private final StoryService storyService;

    @Autowired
    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping
    public ResponseEntity<?> findStory(@RequestParam("name") String name) {
        Optional<Story> story = storyService.findStoryWithName(name);
        return story.<ResponseEntity<Object>>map(value -> ResponseEntity.ok(new Responses.StoryResponse(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Responses.StoryNotFoundResponse("Story not found")));
    }

    @PutMapping("/{id}/tasks/add")
    public ResponseEntity<?> addTasks(@PathVariable("id") long id, @Valid @RequestBody List<AddTaskDto> addTaskDtos) {
        try {
            Story story = storyService.createTasksForStory(id, addTaskDtos);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Responses.StoryResponse(story));
        } catch (StoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StoryNotFoundException("Story not found"));
        } catch (TasksNamesConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Responses.TaskNamesConflictResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<?> renameStory(@PathVariable("id") long id, @Valid @RequestBody RenameStoryDto dto) {
        try {
            Story story = storyService.renameStory(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.StoryUpdatedResponse(story));
        } catch (StoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.StoryNotFoundResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/priority/update")
    public ResponseEntity<?> updateStoryPriority(
            @PathVariable("id") long id,
            @Valid @RequestBody UpdateStoryPriorityDto dto
    ) {
        try {
            Story story = storyService.updateStoryPriority(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.StoryResponse(story));
        } catch (StoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.StoryNotFoundResponse(e.getMessage()));
        } catch (UnknownPriorityTypeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.UnknownOperationResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/progress/update")
    public ResponseEntity<?> updateStoryProgress(
            @PathVariable("id") long id,
            @Valid @RequestBody UpdateStoryProgressDto dto
    ) {
        try {
            Story story = storyService.updateStoryProgress(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.StoryResponse(story));
        } catch (StoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.StoryNotFoundResponse(e.getMessage()));
        } catch (UnknownProgressTypeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.UnknownOperationResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/resolution/update")
    public ResponseEntity<?> updateStoryResolution(
            @PathVariable("id") long id,
            @Valid @RequestBody UpdateStoryResolutionDto dto
    ) {
        try {
            Story story = storyService.updateStoryResolution(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.StoryResponse(story));
        } catch (StoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.StoryNotFoundResponse(e.getMessage()));
        } catch (UnknownProgressTypeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.UnknownOperationResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/estimate/update")
    public ResponseEntity<?> updateOriginalEstimate(
            @PathVariable("id") long id,
            @RequestBody UpdateEstimateDto dto
    ) {
        try {
            Story story = storyService.updateStoryOriginalEstimate(id, dto);
            return ResponseEntity.ok(new Responses.StoryResponse(story));
        } catch (StoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.StoryNotFoundResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/estimate/remaining/update")
    public ResponseEntity<?> updateRemainingEstimate(
            @PathVariable("id") long id,
            @RequestBody UpdateEstimateDto dto) {
        try {
            Story story = storyService.updateRemainingEstimate(id, dto);
            return ResponseEntity.ok(new Responses.StoryResponse(story));
        } catch (StoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.StoryNotFoundResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/storypoints/update")
    public ResponseEntity<?> updateStoryPoints(@PathVariable("id") long id, @RequestBody UpdateStoryPointsDto dto) {
        try {
            Story story = storyService.updateStoryPoints(id, dto);
            return ResponseEntity.ok(new Responses.StoryResponse(story));
        } catch (StoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.StoryNotFoundResponse(e.getMessage()));
        }
    }
}
