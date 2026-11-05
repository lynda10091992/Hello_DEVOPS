package devops;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Endpoint
public class ParticipantSoapEndpoint {

    private static final String NAMESPACE = "urn:hello-devops:participants";
    private final ParticipantRepository repository;

    public ParticipantSoapEndpoint(ParticipantRepository repository) {
        this.repository = repository;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "chercherParticipantRequest")
    @ResponsePayload
    public Element chercher(@RequestPayload Element demande)
            throws ParserConfigurationException {

        // Lire l'identifiant dans la demande XML.
        var ids = demande.getElementsByTagNameNS(NAMESPACE, "id");
        if (ids.getLength() != 1) {
            throw new IllegalArgumentException("La demande doit contenir un seul id.");
        }
        int id = Integer.parseInt(ids.item(0).getTextContent().trim());

        // Chercher la fiche dans PostgreSQL.
        var participant = repository.chercherParId(id);

        // Construire le contenu XML de la réponse.
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element reponse = document.createElementNS(NAMESPACE, "chercherParticipantResponse");
        document.appendChild(reponse);

        ajouterChamp(reponse, "trouve", Boolean.toString(participant.isPresent()));
        participant.ifPresent(fiche -> {
            ajouterChamp(reponse, "id", Integer.toString(fiche.id()));
            ajouterChamp(reponse, "prenom", fiche.prenom());
            ajouterChamp(reponse, "age", Integer.toString(fiche.age()));
            ajouterChamp(reponse, "ville", fiche.ville());
        });

        return reponse;
    }

    private void ajouterChamp(Element parent, String nom, String valeur) {
        Element champ = parent.getOwnerDocument().createElementNS(NAMESPACE, nom);
        champ.setTextContent(valeur);
        parent.appendChild(champ);
    }
}
