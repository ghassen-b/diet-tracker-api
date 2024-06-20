package com.example.diet_tracker_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handler for all NOT_FOUND-related exceptions.
     */
    @ExceptionHandler(value = { MealNotFoundException.class, UserNotFoundException.class })
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleItemNotFoundExceptions(RuntimeException exception) {
        return exception.getMessage();
    }

    /**
     * Handler for all UNPROCESSABLE_ENTITY-related exceptions.
     */
    @ExceptionHandler(value = { MealInvalidInputException.class })
    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public String handleInvalidInput(RuntimeException exception) {
        return exception.getMessage();
    }
}
