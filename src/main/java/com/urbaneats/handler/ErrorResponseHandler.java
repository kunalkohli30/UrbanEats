package com.urbaneats.handler;

import com.urbaneats.dto.error.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponseHandler {

    public static ResponseEntity<?>  respondError(Error error) {

        return switch (error.getErrorType()){
            case AUTH_TOKEN_INVALID -> new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
            case EMAIL_ALREADY_REGISTERED, RESTAURANT_ALREADY_PRESENT_IN_FAVOURITES, CART_RESTAURANT_ID_MISMATCH -> new ResponseEntity<>(error, HttpStatus.CONFLICT);
            case ADDRESS_NAME_ALREADY_EXISTS, RESTAURANT_DOES_NOT_EXIST -> new ResponseEntity<>(error, HttpStatus.PRECONDITION_FAILED);
            case RESTAURANT_NOT_FOUND, USER_NOT_FOUND -> new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            case CART_DOES_NOT_EXIST, FOOD_ID_INVALID -> new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            case INTERNAL_SERVER_ERROR -> new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
            default -> new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}
