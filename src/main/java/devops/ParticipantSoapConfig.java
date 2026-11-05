package devops;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@Configuration
public class ParticipantSoapConfig {

    // Spring charge et initialise le fichier XSD.
    @Bean
    public SimpleXsdSchema participantsSchema() {
        return new SimpleXsdSchema(
                new ClassPathResource("participants.xsd")
        );
    }

    // Le WSDL utilise le schéma déjà initialisé par Spring.
    @Bean
    public DefaultWsdl11Definition participants(
            SimpleXsdSchema participantsSchema) {

        var wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("ParticipantsPort");
        wsdl.setLocationUri("http://127.0.0.1:8083/services/");
        wsdl.setSchema(participantsSchema);

        return wsdl;
    }
}
