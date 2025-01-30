package com.example.diet_tracker_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.diet_tracker_api.model.Meal;

/**
 * DAO for for meals.
 */
public interface MealDAO extends JpaRepository<Meal, Long> {
    /**
     * Finds all meals whose userId column matches the provided userId value.
     *
     * @param userId userId who created the Meal instance
     * @return List of Meals for the given userId
     */
    List<Meal> findByUserId(String userId);

    /**
     * Finds a given meal matching the (meal) id & userId.
     *
     * @param id     Meal id to match
     * @param userId user id to match
     * @return The optional matching meal
     */
    Optional<Meal> findByIdAndUserId(Long id, String userId);

    /**
     * Finds whether a given meal matching the (meal) id & userId exists.
     *
     * @param id     Meal id to match
     * @param userId user id to match
     * @return Whether the meal exists or not
     */
    Boolean existsByIdAndUserId(Long id, String userId);

}
