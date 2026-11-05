package devops;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParticipantController {

    private final ParticipantRepository repository;

    public ParticipantController(ParticipantRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/participants/{id}")
    public ResponseEntity<Participant> chercher(@PathVariable("id") int id) {
        // Cherche la fiche dans PostgreSQL et renvoie 404 si elle est absente.
        return ResponseEntity.of(repository.chercherParId(id));
    }
}
