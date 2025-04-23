package eu.flare.repository.story;

import eu.flare.model.StoryPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryPriorityRepository extends JpaRepository<StoryPriority, Long> {
    Optional<StoryPriority> findByName(String name);
}
