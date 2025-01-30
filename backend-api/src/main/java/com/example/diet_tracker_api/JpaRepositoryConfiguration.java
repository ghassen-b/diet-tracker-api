package com.example.diet_tracker_api;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(enableDefaultTransactions = false) // This prevents Spring Data JPA from making every Repository
                                                          // method transactional, leading to potential non-ACID
                                                          // behavior
public class JpaRepositoryConfiguration {

}
