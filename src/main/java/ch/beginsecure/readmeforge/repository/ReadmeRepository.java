package ch.beginsecure.readmeforge.repository;

import ch.beginsecure.readmeforge.model.Readme;
import ch.beginsecure.readmeforge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadmeRepository extends JpaRepository<Readme, Long> {
    List<Readme> findByOwner(User owner);
}
