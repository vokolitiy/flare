package eu.flare.repository.task;

import eu.flare.model.TaskResolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskResolutionRepository extends JpaRepository<TaskResolution, Long> {
    Optional<TaskResolution> findByName(String name);
}
