package devops;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantApiTest {

    private static final String NS = "urn:hello-devops:participants";
    private final Participant lynda = new Participant(2, "Lynda", 34, "Cergy");

    // REST : une fiche trouvée doit être renvoyée avec HTTP 200.
    @Test
    void restRenvoieLaFiche() {
        var repository = mock(ParticipantRepository.class);
        when(repository.chercherParId(2)).thenReturn(Optional.of(lynda));

        var reponse = new ParticipantController(repository).chercher(2);

        assertEquals(200, reponse.getStatusCode().value());
        assertEquals(lynda, reponse.getBody());
        verify(repository).chercherParId(2);
    }

    // REST : une fiche absente doit produire HTTP 404.
    @Test
    void restRenvoie404() {
        var repository = mock(ParticipantRepository.class);
        when(repository.chercherParId(9999)).thenReturn(Optional.empty());

        var reponse = new ParticipantController(repository).chercher(9999);

        assertEquals(404, reponse.getStatusCode().value());
        assertNull(reponse.getBody());
    }

    // Repository : vérifier la requête et la conversion d'une ligne SQL.
    @Test
    void repositoryTransformeUneLigneEnParticipant() throws Exception {
        var jdbc = mock(JdbcTemplate.class);
        var ligne = mock(ResultSet.class);

        when(ligne.getInt("id")).thenReturn(2);
        when(ligne.getString("prenom")).thenReturn("Lynda");
        when(ligne.getInt("age")).thenReturn(34);
        when(ligne.getString("ville")).thenReturn("Cergy");

        String sql = "SELECT id, prenom, age, ville FROM public.participant WHERE id = ?";

        when(jdbc.query(
                eq(sql),
                ArgumentMatchers.<RowMapper<Participant>>any(),
                eq(2)
        )).thenAnswer(appel -> {
            RowMapper<Participant> conversion = appel.getArgument(1);
            return List.of(conversion.mapRow(ligne, 0));
        });

        var resultat = new ParticipantRepository(jdbc).chercherParId(2);

        assertEquals(Optional.of(lynda), resultat);
        verify(jdbc).query(
                eq(sql),
                ArgumentMatchers.<RowMapper<Participant>>any(),
                eq(2)
        );
    }

    // Repository : gérer une recherche sans résultat.
    @Test
    void repositoryGereUneFicheAbsente() {
        var jdbc = mock(JdbcTemplate.class);

        when(jdbc.query(
                anyString(),
                ArgumentMatchers.<RowMapper<Participant>>any(),
                eq(9999)
        )).thenReturn(List.of());

        var resultat = new ParticipantRepository(jdbc).chercherParId(9999);

        assertTrue(resultat.isEmpty());
    }

    // SOAP : vérifier tous les champs de la réponse XML.
    @Test
    void soapRenvoieLaFiche() throws Exception {
        var repository = mock(ParticipantRepository.class);
        when(repository.chercherParId(2)).thenReturn(Optional.of(lynda));

        var reponse = new ParticipantSoapEndpoint(repository).chercher(demande("2"));

        assertEquals("chercherParticipantResponse", reponse.getLocalName());
        assertEquals(NS, reponse.getNamespaceURI());
        assertEquals("true", champ(reponse, "trouve"));
        assertEquals("2", champ(reponse, "id"));
        assertEquals("Lynda", champ(reponse, "prenom"));
        assertEquals("34", champ(reponse, "age"));
        assertEquals("Cergy", champ(reponse, "ville"));
    }

    // SOAP : une fiche absente donne trouve=false.
    @Test
    void soapGereUneFicheAbsente() throws Exception {
        var repository = mock(ParticipantRepository.class);
        when(repository.chercherParId(9999)).thenReturn(Optional.empty());

        var reponse = new ParticipantSoapEndpoint(repository).chercher(demande("9999"));

        assertEquals("false", champ(reponse, "trouve"));
        assertEquals(0, reponse.getElementsByTagNameNS(NS, "id").getLength());
    }

    // SOAP : refuser une demande sans identifiant.
    @Test
    void soapRefuseUnIdentifiantAbsent() throws Exception {
        var repository = mock(ParticipantRepository.class);
        var endpoint = new ParticipantSoapEndpoint(repository);
        var requete = demande(null);

        assertThrows(IllegalArgumentException.class, () -> endpoint.chercher(requete));
        verifyNoInteractions(repository);
    }

    // SOAP : refuser un identifiant qui n'est pas un entier.
    @Test
    void soapRefuseUnIdentifiantInvalide() throws Exception {
        var repository = mock(ParticipantRepository.class);
        var endpoint = new ParticipantSoapEndpoint(repository);
        var requete = demande("bonjour");

        assertThrows(NumberFormatException.class, () -> endpoint.chercher(requete));
        verifyNoInteractions(repository);
    }

    // WSDL : vérifier que le XSD permet de générer le contrat attendu.
    @Test
    void configurationGenereLeWsdl() throws Exception {
        var configuration = new ParticipantSoapConfig();
        var schema = configuration.participantsSchema();
        schema.afterPropertiesSet();

        assertEquals(NS, schema.getTargetNamespace());

        var wsdl = configuration.participants(schema);
        wsdl.afterPropertiesSet();

        var texte = new StringWriter();
        TransformerFactory.newInstance().newTransformer()
                .transform(wsdl.getSource(), new StreamResult(texte));

        assertTrue(texte.toString().contains("chercherParticipant"));
        assertTrue(texte.toString().contains("ParticipantsPort"));
        assertTrue(texte.toString().contains("http://127.0.0.1:8083/services/"));
    }

    // Préparer un message XML pour les tests SOAP.
    private Element demande(String id) throws Exception {
        var document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        var racine = document.createElementNS(NS, "chercherParticipantRequest");
        document.appendChild(racine);

        if (id != null) {
            var identifiant = document.createElementNS(NS, "id");
            identifiant.setTextContent(id);
            racine.appendChild(identifiant);
        }

        return racine;
    }

    // Lire un champ dans la réponse XML.
    private String champ(Element reponse, String nom) {
        return reponse.getElementsByTagNameNS(NS, nom).item(0).getTextContent();
    }
}
