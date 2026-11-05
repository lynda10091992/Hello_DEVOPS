package devops;

import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParticipantControllerTest {

    @Test
    void renvoieLaFicheDuParticipant() {
        // Préparer une fiche connue et un Repository simulé.
        var lynda = new Participant(2, "Lynda", 34, "Cergy");
        var repository = mock(ParticipantRepository.class);
        when(repository.chercherParId(2)).thenReturn(Optional.of(lynda));

        // Appeler la méthode du Controller avec le numéro 2.
        var controller = new ParticipantController(repository);
        var reponse = controller.chercher(2);

        // Vérifier le statut et la fiche renvoyée.
        assertEquals(200, reponse.getStatusCode().value());
        assertEquals(lynda, reponse.getBody());
    }
}
