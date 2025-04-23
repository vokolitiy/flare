package eu.flare.controller.story;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.flare.model.StoryResolution;
import eu.flare.service.story.StoryResolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/storyResolution")
public class StoryResolutionController {

    private final StoryResolutionService storyResolutionService;

    @Autowired
    public StoryResolutionController(StoryResolutionService storyResolutionService) {
        this.storyResolutionService = storyResolutionService;
    }

    @GetMapping
    public ResponseEntity<Object> findStoryResolutions() {
        List<StoryResolution> storyResolutions = storyResolutionService.findStoryResolutions();
        if (storyResolutions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StoryResolutionsNotFoundResponse("Story resolutions not found"));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new StoryResolutionsResponse(storyResolutions));
        }
    }

    private record StoryResolutionsNotFoundResponse(@JsonProperty("error") String error){}
    private record StoryResolutionsResponse(@JsonProperty("resolutions") List<StoryResolution> resolutions){}
}
