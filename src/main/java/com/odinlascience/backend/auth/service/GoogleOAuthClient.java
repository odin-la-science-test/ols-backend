package com.odinlascience.backend.auth.service;

import com.odinlascience.backend.auth.config.OAuthProperties;
import com.odinlascience.backend.auth.dto.GoogleUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
public class GoogleOAuthClient {

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    private final RestClient restClient;
    private final OAuthProperties oAuthProperties;

    public GoogleOAuthClient(OAuthProperties oAuthProperties) {
        this.restClient = RestClient.create();
        this.oAuthProperties = oAuthProperties;
    }

    /**
     * Echange le code d'autorisation contre un access token Google.
     * @return l'access token Google (pour appeler userinfo)
     */
    public String exchangeCodeForAccessToken(String code) {
        OAuthProperties.Google google = oAuthProperties.getGoogle();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", google.getClientId());
        body.add("client_secret", google.getClientSecret());
        body.add("redirect_uri", google.getRedirectUri());
        body.add("grant_type", "authorization_code");

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey("access_token")) {
            throw new IllegalStateException("Reponse Google token invalide");
        }

        log.debug("Google token exchange reussi");
        return (String) response.get("access_token");
    }

    /**
     * Recupere les infos utilisateur depuis Google avec l'access token.
     */
    public GoogleUserInfo getUserInfo(String accessToken) {
        GoogleUserInfo userInfo = restClient.get()
                .uri(USERINFO_URL)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(GoogleUserInfo.class);

        if (userInfo == null || userInfo.getEmail() == null) {
            throw new IllegalStateException("Impossible de recuperer les infos utilisateur Google");
        }

        log.debug("Google user info recupere pour: {}", userInfo.getEmail());
        return userInfo;
    }
}
