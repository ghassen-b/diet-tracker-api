package com.example.diet_tracker_api.exception;

/*
 * Exception raised when a User is selected by id but not found in the DB.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super(String.format("User with id=%d not found", id));
    }

}
