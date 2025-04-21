package eu.flare.repository;

import eu.flare.model.Epic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {
    Optional<Epic> findByName(String name);
}
