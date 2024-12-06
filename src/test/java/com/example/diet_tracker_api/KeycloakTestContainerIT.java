package com.example.diet_tracker_api;

import static java.util.Collections.singletonList;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.annotation.JsonProperty;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;

@Testcontainers
public class KeycloakTestContainerIT {

    static final String GRANT_TYPE_PASSWORD = "password";
    static final String CLIENT_ID = "diet-app-client";
    static final String USERNAME = "diet-app-user";
    static final String PASSWORD = "diet-app-user-password";

    static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.0";
    static String realmImportFile = "/keycloak/it-keycloak-data.json";
    static String realmName = "diet-app-it-realm";

    @LocalServerPort
    private int port;

    @Autowired
    OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer(KEYCLOAK_IMAGE)
            .withRealmImportFile(realmImportFile);

    /**
     * Sets the jwt.issuer-uri property based on the KC TestContainer actual URL at runtime.
     * @param registry
     */
    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/" + realmName);
    };

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    /**
     * Returns a token obtained from the KC deployed instance.
     * @return
     */
    protected String getToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", singletonList(GRANT_TYPE_PASSWORD));
        map.put("client_id", singletonList(CLIENT_ID));
        map.put("username", singletonList(USERNAME));
        map.put("password", singletonList(PASSWORD));

        String authServerUrl = oAuth2ResourceServerProperties.getJwt().getIssuerUri() +
                "/protocol/openid-connect/token";

        var request = new HttpEntity<>(map, httpHeaders);
        KeyCloakToken token = restTemplate.postForObject(
                authServerUrl,
                request,
                KeyCloakToken.class);

        assert token != null;
        return token.accessToken();
    }

    record KeyCloakToken(@JsonProperty("access_token") String accessToken) {
    }

}
