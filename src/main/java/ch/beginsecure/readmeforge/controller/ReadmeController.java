package ch.beginsecure.readmeforge.controller;

import ch.beginsecure.readmeforge.model.Readme;
import ch.beginsecure.readmeforge.model.User;
import ch.beginsecure.readmeforge.repository.ReadmeRepository;
import ch.beginsecure.readmeforge.repository.UserRepository; // for delete checks later
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/readmes")
@CrossOrigin(origins = "*")
public class ReadmeController {

    private final ReadmeRepository readmeRepository;

    public ReadmeController(ReadmeRepository readmeRepository) {
        this.readmeRepository = readmeRepository;
    }

    @GetMapping
    public List<Readme> getAll(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return readmeRepository.findByOwner(currentUser);
    }

    @PostMapping
    public Readme create(@RequestBody Readme readme, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot create readme without an authenticated user.");
        }
        readme.setOwner(currentUser);
        return readmeRepository.save(readme);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Readme readmeToDelete = readmeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Readme not found"));

        if (!readmeToDelete.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this readme");
        }

        readmeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}