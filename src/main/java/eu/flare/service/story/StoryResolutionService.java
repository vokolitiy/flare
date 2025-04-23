package eu.flare.service.story;

import eu.flare.model.StoryResolution;
import eu.flare.repository.story.StoryResolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryResolutionService {

    private final StoryResolutionRepository storyResolutionRepository;

    @Autowired
    public StoryResolutionService(StoryResolutionRepository storyResolutionRepository) {
        this.storyResolutionRepository = storyResolutionRepository;
    }

    public List<StoryResolution> findStoryResolutions() {
        return storyResolutionRepository.findAll();
    }
}
