// Classe de l'application, située dans le package devops
import devops.devops;

// Outils JUnit pour déclarer les tests et vérifier les résultats
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DevopsTest {

    // Vérifie que le message correspond exactement au résultat attendu
    @Test
    void messageEstCorrect() {
        assertEquals("Hello World! lynda from group DevOps", devops.message());
    }

    // Vérifie que le message n'est pas vide
    @Test
    void messageNEstPasVide() {
        assertTrue(devops.message().length() > 0);
    }
}
