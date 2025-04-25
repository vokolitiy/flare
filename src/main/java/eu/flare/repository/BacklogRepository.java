package eu.flare.repository;

import eu.flare.model.Backlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BacklogRepository extends JpaRepository<Backlog, Long> {
    Optional<Backlog> findByName(String name);
}
