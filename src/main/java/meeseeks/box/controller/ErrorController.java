package meeseeks.box.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ErrorController
{
    /**
     * Catches errors of the specified type and return 404 responses instead.
     */
    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<Object>("{\"error\": \"" + ex.getMessage() + "\"}", HttpStatus.NOT_FOUND);
    }
}
