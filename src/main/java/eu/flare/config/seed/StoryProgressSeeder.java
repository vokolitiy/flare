package eu.flare.config.seed;

import eu.flare.model.StoryProgress;
import eu.flare.repository.story.StoryProgressRepository;

import java.util.List;
import java.util.Optional;

public class StoryProgressSeeder extends DataSeeder {

    public StoryProgressSeeder(StoryProgressRepository repository) {
        super(repository);
    }

    @Override
    public void createDataIfNotExists(List<String> data) {
        StoryProgressRepository progressRepository = (StoryProgressRepository) repository;
        data.forEach(item -> {
            Optional<StoryProgress> progressOptional = progressRepository.findByName(item);
            if (progressOptional.isEmpty()) {
                StoryProgress storyProgress = new StoryProgress();
                storyProgress.setName(item);
                progressRepository.save(storyProgress);
            }
        });
    }
}
