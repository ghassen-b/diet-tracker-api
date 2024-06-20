package com.example.diet_tracker_api.exception;

import com.example.diet_tracker_api.dto.MealInDTO;

/**
 * Exception thrown when an invalid MealInDTO is provided.
 */
public class MealInvalidInputException extends RuntimeException {
    public MealInvalidInputException(MealInDTO mealInDTO, String message) {
        super(String.format("Meal creation/update failed because of invalid input.\nInput: %s\nError: %s", mealInDTO,
                message));
    }
}
