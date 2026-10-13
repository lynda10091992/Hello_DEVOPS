import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DevopsTest {

    // Test 1 : le message est exactement celui attendu
    @Test
    void messageEstCorrect() {
        assertEquals("Hello World! lynda from group DevOps", devops.message());
    }

    // Test 2 : le message n'est pas vide
    @Test
    void messageNEstPasVide() {
        assertTrue(devops.message().length() > 0);
    }
}
