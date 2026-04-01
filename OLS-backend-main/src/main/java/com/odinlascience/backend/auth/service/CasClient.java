package com.odinlascience.backend.auth.service;

import com.odinlascience.backend.auth.config.OAuthProperties;
import com.odinlascience.backend.auth.dto.CasUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Slf4j
@Service
public class CasClient {

    private static final String CAS_NAMESPACE = "http://www.yale.edu/tp/cas";

    private final RestClient restClient;
    private final OAuthProperties oAuthProperties;

    public CasClient(OAuthProperties oAuthProperties) {
        this.restClient = RestClient.create();
        this.oAuthProperties = oAuthProperties;
    }

    /**
     * Valide un ticket CAS aupres du serveur CAS et retourne les infos utilisateur.
     */
    public CasUserInfo validateTicket(String ticket, String serviceUrl) {
        String casServerUrl = oAuthProperties.getCas().getServerUrl();
        String validationUrl = casServerUrl + "/serviceValidate?ticket=" + ticket + "&service=" + serviceUrl;

        log.debug("Validation CAS ticket aupres de: {}", validationUrl);

        String xmlResponse = restClient.get()
                .uri(validationUrl)
                .retrieve()
                .body(String.class);

        if (xmlResponse == null) {
            throw new IllegalStateException("Reponse CAS vide");
        }

        return parseCasResponse(xmlResponse);
    }

    private CasUserInfo parseCasResponse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            // Protection XXE
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            Document doc = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));

            // Verifier si l'authentification a echoue
            NodeList failureNodes = doc.getElementsByTagNameNS(CAS_NAMESPACE, "authenticationFailure");
            if (failureNodes.getLength() > 0) {
                String errorMessage = failureNodes.item(0).getTextContent().trim();
                throw new IllegalStateException("Authentification CAS echouee: " + errorMessage);
            }

            // Extraire les attributs de l'authentification reussie
            NodeList successNodes = doc.getElementsByTagNameNS(CAS_NAMESPACE, "authenticationSuccess");
            if (successNodes.getLength() == 0) {
                throw new IllegalStateException("Reponse CAS invalide: ni succes ni echec");
            }

            Element success = (Element) successNodes.item(0);
            String uid = getElementText(success, "user");

            // Les attributs sont dans <cas:attributes>
            String email = getAttributeValue(success, "mail");
            String firstName = getAttributeValue(success, "givenName");
            String lastName = getAttributeValue(success, "sn");

            // Fallback: si pas d'email dans les attributs, utiliser uid@univ-lille.fr
            if (email == null || email.isBlank()) {
                email = uid + "@univ-lille.fr";
            }

            log.debug("CAS validation reussie pour: {}", uid);

            return CasUserInfo.builder()
                    .uid(uid)
                    .email(email)
                    .firstName(firstName != null ? firstName : "")
                    .lastName(lastName != null ? lastName : "")
                    .build();

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors du parsing de la reponse CAS", e);
        }
    }

    private String getElementText(Element parent, String localName) {
        NodeList nodes = parent.getElementsByTagNameNS(CAS_NAMESPACE, localName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }

    private String getAttributeValue(Element success, String attributeName) {
        NodeList attrNodes = success.getElementsByTagNameNS(CAS_NAMESPACE, "attributes");
        if (attrNodes.getLength() > 0) {
            Element attributes = (Element) attrNodes.item(0);
            NodeList values = attributes.getElementsByTagNameNS(CAS_NAMESPACE, attributeName);
            if (values.getLength() > 0) {
                return values.item(0).getTextContent().trim();
            }
        }
        return null;
    }
}
