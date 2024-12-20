package com.example.diet_tracker_api.exception;

/*
 * Exception raised when a Meal is selected by id but not found in the DB.
 */
public class MealNotFoundException extends RuntimeException {
    public MealNotFoundException(String userId, Long id) {
        super(String.format("Meal with id=%d not found for userId=%s", id, userId));
    }

}
