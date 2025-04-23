package eu.flare.config.seed;

import eu.flare.model.StoryResolution;
import eu.flare.repository.story.StoryResolutionRepository;

import java.util.List;
import java.util.Optional;

public class StoryResolutionSeeder extends DataSeeder {

    public StoryResolutionSeeder(StoryResolutionRepository repository) {
        super(repository);
    }

    @Override
    public void createDataIfNotExists(List<String> data) {
        StoryResolutionRepository storyResolutionRepository = (StoryResolutionRepository) repository;
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
