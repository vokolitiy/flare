package eu.flare.controller;

import eu.flare.exceptions.conflicts.StoryNamesConflictException;
import eu.flare.exceptions.empty.RequestBodyEmptyException;
import eu.flare.exceptions.notfound.EpicNotFoundException;
import eu.flare.model.Epic;
import eu.flare.model.dto.add.AddStoryDto;
import eu.flare.model.dto.rename.RenameEpicDto;
import eu.flare.model.response.Responses;
import eu.flare.service.EpicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/epic")
public class EpicController {

    private final EpicService epicService;

    @Autowired
    public EpicController(EpicService epicService) {
        this.epicService = epicService;
    }

    @GetMapping
    public ResponseEntity<Object> findEpic(@RequestParam("name") String name) {
        Optional<Epic> epic = epicService.findEpic(name);
        return epic.<ResponseEntity<Object>>map(value -> ResponseEntity.status(HttpStatus.OK)
                .body(new Responses.EpicResponse(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Responses.EpicNotFoundResponse("Epic not found")));
    }

    @PutMapping("/{id}/stories/add")
    public ResponseEntity<Object> addStories(@PathVariable("id") long id, @RequestBody List<AddStoryDto> dtos) {
        try {
            Epic epic = epicService.addStoriesForEpic(id, dtos);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Responses.StoryCreatedResponse(epic));
        } catch (EpicNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.EpicNotFoundResponse(e.getMessage()));
        } catch (RequestBodyEmptyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.RequestBodyEmptyResponse(e.getMessage()));
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.UserNotFoundException(exception.getMessage()));
        } catch (StoryNamesConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Responses.StoryCreationException(e.getMessage()));
        }
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<Object> renameEpic(@PathVariable("id") long id, @RequestBody RenameEpicDto epicDto) {
        try {
            Epic epic = epicService.renameEpic(id, epicDto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.EpicResponse(epic));
        } catch (EpicNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.EpicNotFoundResponse(e.getMessage()));
        }
    }
}
