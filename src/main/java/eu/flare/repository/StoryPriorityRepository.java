package eu.flare.repository;

import eu.flare.model.StoryPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryPriorityRepository extends JpaRepository<StoryPriority, Long> {
}
