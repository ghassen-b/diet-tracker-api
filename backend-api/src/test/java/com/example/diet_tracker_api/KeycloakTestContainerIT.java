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
    /**
     * Not allowed (i.e. not having the user role; e.g. user from another app) user credentials.
     */
    static final String NONUSER_USERNAME = "not-a-diet-app-user";
    static final String NONUSER_PASSWORD = "not-a-diet-app-user-password";
    /**
     * Simple user credentials.
     */
    static final String USER_USERNAME = "diet-app-user";
    static final String USER_PASSWORD = "diet-app-user-password";
    /**
     * Admin user credentials.
     */
    static final String ADMIN_USERNAME = "diet-app-admin";
    static final String ADMIN_PASSWORD = "diet-app-admin-password";

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
     * Sets the jwt.issuer-uri property based on the KC TestContainer actual URL at
     * runtime.
     *
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
     * Returns a user-only token obtained from the KC deployed instance.
     *
     * @return
     */
    protected String getNonUserToken() {
        return getToken(NONUSER_USERNAME, NONUSER_PASSWORD);
    }

    /**
     * Returns a user-only token obtained from the KC deployed instance.
     *
     * @return
     */
    protected String getUserToken() {
        return getToken(USER_USERNAME, USER_PASSWORD);
    }

    /**
     * Returns an admin token obtained from the KC deployed instance.
     *
     * @return
     */
    protected String getAdminToken() {
        return getToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    /**
     * Returns a token obtained from the KC deployed instance, using the provided
     * username & password.
     *
     * @return the token
     */
    protected String getToken(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", singletonList(GRANT_TYPE_PASSWORD));
        map.put("client_id", singletonList(CLIENT_ID));
        map.put("username", singletonList(username));
        map.put("password", singletonList(password));

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
