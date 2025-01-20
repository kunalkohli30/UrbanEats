package com.urbaneats.exceptionHandler;

import com.urbaneats.exception.AuthorizationFailedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

//@Configuration
@RestControllerAdvice
public class BadRequestExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(NoResourceFoundException.class)
//    public ResponseEntity<?> noResourceFoundExceptionHandler(HttpServletRequest httpServletRequest, NoResourceFoundException ex) {
//
//        return new ResponseEntity<>(Map.ofEntries(
//                Map.entry("Status", "404"),
//                Map.entry("Error", "Not Found"),
//                Map.entry("Message", "Invalid Request"),
//                Map.entry("Path", httpServletRequest.getRequestURL().toString())
//        ), HttpStatus.NOT_FOUND);
//    }

    @ExceptionHandler(value = AuthorizationFailedException.class)
    public ResponseEntity<?> AuthorizationFailedExceptionHandler(AuthorizationFailedException ex) {

        return new ResponseEntity<>(Map.ofEntries(
//                Map.entry("Status", "401"),
                Map.entry("Error", ex.getErrorCode()),
                Map.entry("Message", ex.getMessage())
//                Map.entry("Path", httpServletRequest.getRequestURL().toString())
        ), HttpStatus.UNAUTHORIZED);
    }
}