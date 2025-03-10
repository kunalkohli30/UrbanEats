package com.urbaneats.dto.error;

public enum ErrorType {

    NOT_FOUND,
    EMAIL_ALREADY_REGISTERED,
    USER_NOT_FOUND,
    RESTAURANT_NOT_FOUND,
    BAD_AUTHENTICATION_CREDENTIALS,
    AUTH_TOKEN_EXPIRED,
    AUTH_TOKEN_INVALID,
    REFRESH_TOKEN_INVALID,
    UPDATE_FAILED,
    RESTAURANT_ALREADY_PRESENT_IN_FAVOURITES,
    USER_REGISTRATION_FAILED,
    FIREBASE_EXCEPTION,
    ADDRESS_NAME_ALREADY_EXISTS,
    CART_RESTAURANT_ID_MISMATCH,
    CART_DOES_NOT_EXIST,
    FOOD_ID_INVALID,
    VALIDATION_FAILED,
    FAILED_TO_CREATE_PAYMENT_ORDER,
    RAZORPAY_ERROR,
    RESTAURANT_DOES_NOT_EXIST,
    INTERNAL_SERVER_ERROR
}
