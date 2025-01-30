package com.example.diet_tracker_api.exception;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.MappingException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handler for all the exceptions raised by the Model Mapper instance.
     *
     * @param exception RuntimeException raised
     * @return ResponseEntity with the exception msg
     */
    @ExceptionHandler(value = { MappingException.class })
    public ResponseEntity<Object> handleMappingExceptionExceptions(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handler for all NOT_FOUND-related exceptions.
     *
     * @param exception RuntimeException raised
     * @return ResponseEntity with the exception msg
     */
    @ExceptionHandler(value = { MealNotFoundException.class })
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleItemNotFoundExceptions(RuntimeException exception) {
        return exception.getMessage();
    }

    /**
     * Custom handling of errors related to an invalid request body value.
     * /!\ If multiple invalid values are found, the InvalidFormatException is
     * raised only on the first one.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        Throwable cause = ex.getCause();
        Map<String, String> errors = new HashMap<>();

        if (cause instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) cause;
            String fieldName = invalidFormatException.getPath().get(0).getFieldName();
            String invalidValue = invalidFormatException.getValue().toString();
            String errorMessage = "Invalid value for field '" + fieldName + "': " + invalidValue;
            errors.put(fieldName, errorMessage);
        } else {
            errors.put("error", "Malformed JSON request");
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Custom handling of errors related to missing fields.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Custom handling of errors related to an invalid (path) parameter value.
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            @NonNull TypeMismatchException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        String fieldName = ex.getPropertyName();
        String errorMessage = "Invalid value for parameter '" + fieldName + "': " + ex.getValue();
        errors.put(fieldName, errorMessage);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
