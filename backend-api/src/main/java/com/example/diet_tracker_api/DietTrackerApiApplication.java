package com.example.diet_tracker_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(enableDefaultTransactions = false) // This prevents Spring Data JPA from making every Repository
                                                          // method transactional, leading to potential non-ACID
                                                          // behavior
public class DietTrackerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietTrackerApiApplication.class, args);
    }

}
