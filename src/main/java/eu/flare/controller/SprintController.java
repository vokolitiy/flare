package eu.flare.controller;

import eu.flare.exceptions.conflicts.SprintAlreadyStartedException;
import eu.flare.exceptions.empty.RequestBodyEmptyException;
import eu.flare.exceptions.misc.SprintAlreadyCompletedException;
import eu.flare.exceptions.misc.SprintNotStartedException;
import eu.flare.exceptions.notfound.SprintNotFoundException;
import eu.flare.model.Sprint;
import eu.flare.model.dto.add.AddSprintStoryDto;
import eu.flare.model.dto.rename.RenameSprintDto;
import eu.flare.model.response.Responses;
import eu.flare.service.SprintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sprint")
public class SprintController {

    private final SprintService sprintService;

    @Autowired
    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @GetMapping
    public ResponseEntity<?> findSprint(@RequestParam("name") String name) {
        try {
            Sprint sprint = sprintService.findSprint(name);
            return ResponseEntity.ok(new Responses.SprintResponse(sprint));
        } catch (SprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.SprintNotFoundResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<?> renameSprint(@PathVariable("id") long id, @Valid @RequestBody RenameSprintDto dto) {
        try {
            Sprint sprint = sprintService.renameSprint(id, dto);
            return ResponseEntity.ok(new Responses.SprintResponse(sprint));
        } catch (SprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.SprintNotFoundResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startSprint(@PathVariable("id") long id) {
        try {
            Sprint sprint = sprintService.startSprint(id);
            return ResponseEntity.ok(new Responses.SprintResponse(sprint));
        } catch (SprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.SprintNotFoundResponse(e.getMessage()));
        } catch (SprintAlreadyStartedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Responses.SprintAlreadyStartedResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> finishSprint(@PathVariable("id") long id) {
        try {
            Sprint sprint = sprintService.finishSprint(id);
            return ResponseEntity.ok(new Responses.SprintResponse(sprint));
        } catch (SprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.SprintNotFoundResponse(e.getMessage()));
        } catch (SprintNotStartedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.SprintNotStartedResponse(e.getMessage()));
        } catch (SprintAlreadyCompletedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.SprintAlreadyCompletedResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/stories/add")
    public ResponseEntity<?> addStoriesToSprint(
            @PathVariable("id") long id,
            @RequestBody List<AddSprintStoryDto> sprintStories
    ) {
        try {
            Sprint sprint = sprintService.addSprintStories(id, sprintStories);
            return ResponseEntity.ok(new Responses.SprintResponse(sprint));
        } catch (SprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.SprintNotFoundResponse(e.getMessage()));
        } catch (SprintAlreadyCompletedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.SprintAlreadyCompletedResponse(e.getMessage()));
        } catch (RequestBodyEmptyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Responses.RequestBodyEmptyResponse(e.getMessage()));
        }
    }
}
