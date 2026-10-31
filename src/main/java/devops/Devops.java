package devops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Configure et démarre l'application Spring Boot
@SpringBootApplication
public class Devops {

    // Message partagé, dont la valeur ne peut pas être réaffectée
    public static final String MESSAGE =
            "Hello World! lynda from group DevOps";

    public static void main(String[] args) {
        // Démarre l'application avec Tomcat embarqué
        SpringApplication.run(Devops.class, args);
    }
}
