package eu.flare.repository;

import eu.flare.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    Optional<Sprint> findByName(String name);
}
