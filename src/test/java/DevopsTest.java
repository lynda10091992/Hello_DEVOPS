// La classe Devops appartient au package devops
import devops.Devops;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DevopsTest {

    // Vérifie le contenu exact du message
    @Test
    void messageEstCorrect() {
        assertEquals("Hello World! lynda from group DevOps", Devops.MESSAGE);
    }

    // Vérifie que le message n'est pas vide
    @Test
    void messageNEstPasVide() {
        assertTrue(Devops.MESSAGE.length() > 0);
    }
}
