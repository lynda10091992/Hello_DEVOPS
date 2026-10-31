package devops;

// Permet de démarrer l'application Spring Boot
import org.springframework.boot.SpringApplication;

// Active la configuration automatique et la recherche des composants
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class devops {

    // Conserve le message utilisé par nos tests
    public static String message() {
        return "Hello World! lynda from group DevOps";
    }

    public static void main(String[] args) {
        // Démarre Spring Boot et le serveur Tomcat embarqué
        SpringApplication.run(devops.class, args);
    }
}
