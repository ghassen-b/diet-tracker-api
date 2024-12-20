package com.example.diet_tracker_api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

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
    Logger logger = LoggerFactory.getLogger(TrustStorePropertySetter.class);

    // Custom truststore path to use
    @Value("${custom.ssl.trustStore}")
    private String trustStore;

    // Custom truststore password
    @Value("${custom.ssl.trustStorePassword}")
    private String trustStorePassword;

    @PostConstruct
    public void setProperty() {
        logger.warn("Adding a custom file to the TrustStore. This feature should NOT be used in production");
        System.setProperty("javax.net.ssl.trustStore", trustStore);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    @PreDestroy
    public void resetProperty() {
        /*
         * This is step is necessary for two reasons:
         * 1- Well, as a general rule, we shouldn't leave these properties in the wild
         * 2- Not clearing this property breaks the ITs.
         * 
         * Here's a more thourough explanation of the item #2, in a chronological order:
         * a- JVM starts w/o these properties, Spring beans are created
         * b- After the beans are created, we set these properties in the @PostConstruct step
         * c- The tests run, until one of them declares @DirtiesContext: the app context needs to be restarted
         * d- Becase of item #b, javax.net.ssl.trustStore is set to a value read from the 
         *  src/main/resources/application.properties
         * e- It does not find that file (it is a path relative to the root folder, meant to be reachable from there)
         * f- The application context fails to load
         */
        logger.info("Clearing the custom trustStore property");
        System.clearProperty("javax.net.ssl.trustStore");
        System.clearProperty("javax.net.ssl.trustStorePassword");
    }
}