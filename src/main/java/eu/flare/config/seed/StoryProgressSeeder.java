package eu.flare.config.seed;

import eu.flare.model.StoryProgress;
import eu.flare.repository.story.StoryProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StoryProgressSeeder {

    private StoryProgressRepository storyProgressRepository;

    @Autowired
    public StoryProgressSeeder(StoryProgressRepository storyProgressRepository) {
        this.storyProgressRepository = storyProgressRepository;
    }

    public void createDataIfNotExists(List<String> data) {
        data.forEach(item -> {
            Optional<StoryProgress> progressOptional = storyProgressRepository.findByName(item);
            if (progressOptional.isEmpty()) {
                StoryProgress storyProgress = new StoryProgress();
                storyProgress.setName(item);
                storyProgressRepository.save(storyProgress);
            }
        });
    }
}
