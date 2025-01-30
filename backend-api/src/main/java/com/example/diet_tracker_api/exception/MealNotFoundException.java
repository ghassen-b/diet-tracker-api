package com.example.diet_tracker_api.exception;

/**
 * Exception raised when a Meal is selected by id but not found in the DB.
 */
public class MealNotFoundException extends RuntimeException {
    /**
     * Constructor method used when raising the exception.
     *
     * @param userId User Id
     * @param id     Meal id
     */
    public MealNotFoundException(String userId, Long id) {
        super(String.format("Meal with id=%d not found for userId=%s", id, userId));
    }

}
