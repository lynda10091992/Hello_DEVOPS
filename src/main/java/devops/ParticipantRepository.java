package devops;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantRepository {

    private final JdbcTemplate jdbc;

    public ParticipantRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Participant> chercherParId(int id) {
        // Le ? reçoit la valeur de l'identifiant demandé.
        return jdbc.query(
                "SELECT id, prenom, age, ville FROM public.participant WHERE id = ?",
                (ligne, numero) -> new Participant(
                        ligne.getInt("id"),
                        ligne.getString("prenom"),
                        ligne.getInt("age"),
                        ligne.getString("ville")
                ),
                id
        ).stream().findFirst();
    }
}
