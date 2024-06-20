package com.example.diet_tracker_api.exception;

/*
 * Exception raised when a Meal is selected by id but not found in the DB.
 */
public class MealNotFoundException extends RuntimeException {
    public MealNotFoundException(Long id) {
        super(String.format("Meal with id=%d not found", id));
    }

}
