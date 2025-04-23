package eu.flare.service.story;

import eu.flare.model.StoryPriority;
import eu.flare.repository.story.StoryPriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryPriorityService {

    private final StoryPriorityRepository storyPriorityRepository;

    @Autowired
    public StoryPriorityService(StoryPriorityRepository storyPriorityRepository) {
        this.storyPriorityRepository = storyPriorityRepository;
    }

    public List<StoryPriority> findStoryPriorities() {
        return storyPriorityRepository.findAll();
    }
}
