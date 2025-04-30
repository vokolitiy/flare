package eu.flare.controller.story;

import eu.flare.model.StoryPriority;
import eu.flare.model.response.Responses;
import eu.flare.service.story.StoryPriorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/storyPriority")
public class StoryPriorityController {

    private final StoryPriorityService storyPriorityService;

    @Autowired
    public StoryPriorityController(StoryPriorityService storyPriorityService) {
        this.storyPriorityService = storyPriorityService;
    }

    @GetMapping
    public ResponseEntity<Object> findPriorities() {
        List<StoryPriority> storyPriorities = storyPriorityService.findStoryPriorities();
        if (storyPriorities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.StoryPrioritiesNotFoundResponse("Story priorities not found"));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.StoryPrioritiesResponse(storyPriorities));
        }
    }
}
