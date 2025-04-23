package eu.flare.config.seed;

import eu.flare.model.StoryPriority;
import eu.flare.repository.story.StoryPriorityRepository;

import java.util.List;
import java.util.Optional;

public class StoryPrioritySeeder extends DataSeeder {

    public StoryPrioritySeeder(StoryPriorityRepository repository) {
        super(repository);
    }

    @Override
    public void createDataIfNotExists(List<String> data) {
        StoryPriorityRepository storyPriorityRepository = (StoryPriorityRepository) repository;
        data.forEach(item -> {
            Optional<StoryPriority> storyPriorityOptional = storyPriorityRepository.findByName(item);
            if (storyPriorityOptional.isEmpty()) {
                StoryPriority priority = new StoryPriority();
                priority.setName(item);
                storyPriorityRepository.save(priority);
            }
        });
    }
}
