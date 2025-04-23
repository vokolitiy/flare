package eu.flare.repository.story;

import eu.flare.model.StoryResolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryResolutionRepository extends JpaRepository<StoryResolution, Long> {
    Optional<StoryResolution> findByName(String name);
}
