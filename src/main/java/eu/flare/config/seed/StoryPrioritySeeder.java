package eu.flare.config.seed;

import eu.flare.model.StoryPriority;
import eu.flare.repository.story.StoryPriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
public class StoryPrioritySeeder {

    private StoryPriorityRepository storyPriorityRepository;

    @Autowired
    public StoryPrioritySeeder(StoryPriorityRepository storyPriorityRepository) {
        this.storyPriorityRepository = storyPriorityRepository;
    }

    public void createDataIfNotExists(List<String> data) {
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
