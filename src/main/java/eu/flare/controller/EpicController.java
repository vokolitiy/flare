package eu.flare.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.exceptions.EpicNotFoundException;
import eu.flare.exceptions.RequestBodyEmptyException;
import eu.flare.exceptions.StoryNamesConflictException;
import eu.flare.model.Epic;
import eu.flare.model.dto.AddStoryDto;
import eu.flare.service.EpicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/epic")
public class EpicController {

    private final EpicService epicService;

    @Autowired
    public EpicController(EpicService epicService) {
        this.epicService = epicService;
    }

    @PutMapping("/{name}/stories/add")
    public ResponseEntity<Object> addStories(@PathVariable("name") String name, @RequestBody List<AddStoryDto> dtos) {
        try {
            Epic epic = epicService.addStoriesForEpic(name, dtos);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StoryCreatedResponse(epic));
        } catch (EpicNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new EpicNotFoundResponse(e.getMessage()));
        } catch (RequestBodyEmptyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RequestBodyEmptyResponse(e.getMessage()));
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserNotFoundException(exception.getMessage()));
        } catch (StoryNamesConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new StoryCreationException(e.getMessage()));
        }
    }

    private record EpicNotFoundResponse(@JsonProperty("error") String error) {}
    private record RequestBodyEmptyResponse(@JsonProperty("error") String error) {}
    private record UserNotFoundException(@JsonProperty("error") String error) {}
    private record StoryCreationException(@JsonProperty("error") String error) {}

    private record StoryCreatedResponse(@JsonProperty("epic") Epic epic) {}
}
