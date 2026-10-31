package devops;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Répond aux requêtes HTTP
@RestController
public class HelloController {

    // Affiche le message à l'adresse /
    @GetMapping("/")
    public String accueil() {
        return devops.message();
    }
}
