package eu.flare.controller;

import eu.flare.exceptions.notfound.BacklogNotFoundException;
import eu.flare.exceptions.notfound.StoryNotFoundException;
import eu.flare.model.Backlog;
import eu.flare.model.dto.add.AddBacklogStoryDto;
import eu.flare.model.response.Responses;
import eu.flare.service.BacklogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/backlog")
public class BacklogController {

    private final BacklogService backlogService;

    @Autowired
    public BacklogController(BacklogService backlogService) {
        this.backlogService = backlogService;
    }

    @GetMapping
    public ResponseEntity<Object> findBacklog(@Valid @RequestParam("name") String name) {
        Optional<Backlog> backlog = backlogService.findBacklog(name);
        return backlog.<ResponseEntity<Object>>map(value -> ResponseEntity.status(HttpStatus.OK)
                .body(new Responses.BacklogResponse(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Responses.BacklogNotFoundResponse("Backlog not found")));
    }

    @PutMapping("/{id}/stories/add")
    public ResponseEntity<Object> addStories(@PathVariable("id") long id, @Valid @RequestBody List<AddBacklogStoryDto> dtoList) {
        try {
            Backlog backlog = backlogService.addStoriesToBacklog(id, dtoList);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.BacklogResponse(backlog));
        } catch (BacklogNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.BacklogNotFoundResponse(e.getMessage()));
        } catch (StoryNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
