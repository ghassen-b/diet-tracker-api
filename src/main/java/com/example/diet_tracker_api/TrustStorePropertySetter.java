package com.example.diet_tracker_api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * The following Component solves the following issue:
 * When using self-signed certificates for an other component's server (which
 * this app calls as a client),
 * it is not possible to configure Spring to use a custom truststore file (to
 * authorize a specific certificate)
 * 
 * One solution would be to run:
 * java -Djavax.net.ssl.trustStore=/path/to/truststore.jks
 * -Djavax.net.ssl.trustStorePassword=<pwd> ...
 * 
 * This works file in most cases where you can easily put Java options as an
 * input
 * 
 * However, it does not work / is not convenient in the following cases:
 * 1- Running / debugging the app from your IDE: you need to configure special
 * JVM args
 * 2- When you want to run the app using mvn spring-boot:run
 * 
 * Then comes this Component: it takes some (custom) input from the provided
 * application properties, and manually sets
 * the javax.net.ssl.trustStore & javax.net.ssl.trustStorePassword options, at
 * runtime
 * It is not the ideal solution, but it does work...
 */
@Profile("!production")
@Component
public class TrustStorePropertySetter {
    // Custom truststore path to use
    @Value("${custom.ssl.trustStore}")
    private String trustStore;

    // Custom truststore password
    @Value("${custom.ssl.trustStorePassword}")
    private String trustStorePassword;

    @PostConstruct
    public void setProperty() {
        System.setProperty("javax.net.ssl.trustStore", trustStore);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }
}