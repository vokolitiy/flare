package eu.flare.repository;

import eu.flare.model.StoryProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryProgressRepository extends JpaRepository<StoryProgress, Long> {
}
