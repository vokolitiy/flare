package eu.flare.controller.story;

import eu.flare.model.StoryProgress;
import eu.flare.model.response.Responses;
import eu.flare.service.story.StoryProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/storyProgress")
public class StoryProgressController {

    private final StoryProgressService storyProgressService;

    @Autowired
    public StoryProgressController(StoryProgressService storyProgressService) {
        this.storyProgressService = storyProgressService;
    }

    @GetMapping
    public ResponseEntity<Object> findProgresses() {
        List<StoryProgress> storyProgresses = storyProgressService.findStoryProgresses();
        if (storyProgresses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Responses.StoryProgressesNotFoundResponse("Story progresses not found"));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Responses.StoryProgressesResponse(storyProgresses));
        }
    }
}
