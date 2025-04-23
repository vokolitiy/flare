package eu.flare.repository.story;

import eu.flare.model.StoryProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryProgressRepository extends JpaRepository<StoryProgress, Long> {
    Optional<StoryProgress> findByName(String name);
}
