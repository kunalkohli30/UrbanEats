package com.urbaneats.commons;

import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;

public class Constants {

    public static final String SUCCESS = "SUCCESS";

    public static final String cartRestaurantIdMismatchErrorMessage = "The restaurant ID does not match the restaurant associated with the cart. Please clear the cart before adding items from a different restaurant.";

    public static final Error internalServerError = new Error(ErrorType.INTERNAL_SERVER_ERROR, "OOPS! Some internal error occurred. Sorry for the inconvenience!!!");
}
