package eu.flare.controller.story;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.exceptions.conflicts.TasksNamesConflictException;
import eu.flare.exceptions.notfound.StoryNotFoundException;
import eu.flare.model.Story;
import eu.flare.model.dto.add.AddTaskDto;
import eu.flare.model.dto.rename.RenameStoryDto;
import eu.flare.service.story.StoryService;
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
    public ResponseEntity<Object> findStory(@RequestParam("name") String name) {
        Optional<Story> story = storyService.findStoryWithName(name);
        return story.<ResponseEntity<Object>>map(value -> ResponseEntity.ok(new StoryResponse(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new StoryNotFoundResponse("Story not found")));
    }

    @PutMapping("/{id}/tasks/add")
    public ResponseEntity<Object> addTasks(@PathVariable("id") long id, @RequestBody List<AddTaskDto> addTaskDtos) {
        try {
            Story story = storyService.createTasksForStory(id, addTaskDtos);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StoryResponse(story));
        } catch (StoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StoryNotFoundException("Story not found"));
        } catch (TasksNamesConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new TaskNamesConflictResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<Object> renameStory(@PathVariable("id") long id, @RequestBody RenameStoryDto dto) {
        try {
            Story story = storyService.renameStory(id, dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new StoryUpdatedResponse(story));
        } catch (StoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StoryNotFoundResponse(e.getMessage()));
        }
    }

    private record StoryResponse(
            @JsonProperty("story") Story story
    ){}

    private record TaskNamesConflictResponse(
            @JsonProperty("error") String error
    ) {}

    private record StoryNotFoundResponse(
            @JsonProperty("error") String error
    ){}

    private record StoryUpdatedResponse(
            @JsonProperty("story") Story story
    ) {}
}
