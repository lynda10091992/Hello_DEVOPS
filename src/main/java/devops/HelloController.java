package devops;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Répond aux requêtes HTTP
@RestController
public class HelloController {

    // Renvoie le message pour l'adresse /
    @GetMapping("/")
    public String accueil() {
        return "<h1>" + Devops.MESSAGE + "</h1><p><a href=\"/participants.html\">Rechercher un participant</a></p>";
    }
}
