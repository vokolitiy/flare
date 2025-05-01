package eu.flare.config.seed;

import eu.flare.model.StoryResolution;
import eu.flare.repository.story.StoryResolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
public class StoryResolutionSeeder {

    private StoryResolutionRepository storyResolutionRepository;

    @Autowired
    public StoryResolutionSeeder(StoryResolutionRepository storyResolutionRepository) {
        this.storyResolutionRepository = storyResolutionRepository;
    }

    public void createDataIfNotExists(List<String> data) {
        data.forEach(item -> {
            Optional<StoryResolution> storyResolutionOptional = storyResolutionRepository.findByName(item);
            if (storyResolutionOptional.isEmpty()) {
                StoryResolution storyResolution = new StoryResolution();
                storyResolution.setName(item);
                storyResolutionRepository.save(storyResolution);
            }
        });
    }
}
