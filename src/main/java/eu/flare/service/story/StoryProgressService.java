package eu.flare.service.story;

import eu.flare.model.StoryProgress;
import eu.flare.repository.story.StoryProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryProgressService {

    private final StoryProgressRepository storyProgressRepository;

    @Autowired
    public StoryProgressService(StoryProgressRepository storyProgressRepository) {
        this.storyProgressRepository = storyProgressRepository;
    }

    public List<StoryProgress> findStoryProgresses() {
        return storyProgressRepository.findAll();
    }
}
