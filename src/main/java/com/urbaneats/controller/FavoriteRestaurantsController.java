package com.urbaneats.controller;

import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.restaurant.FavoriteRestaurantRequestDto;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.service.FavoriteRestaurantsService;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/restaurant/favorites")
@Slf4j
public class FavoriteRestaurantsController {

    private final FavoriteRestaurantsService favoriteRestaurantsService;

    public FavoriteRestaurantsController(FavoriteRestaurantsService favoriteRestaurantsService) {
        this.favoriteRestaurantsService = favoriteRestaurantsService;
    }

    @GetMapping
    public ResponseEntity<?> getUserFavoriteRestaurants(HttpServletRequest request) {
        String userId = request.getAttribute("uid").toString();
        return Try.of(() -> favoriteRestaurantsService.getUserFavoriteRestaurants(userId))
                .toEither()
                .peekLeft(throwable -> log.error("Error occurred while retrieving user favourite restaurants for userId: {}." +
                        " Error message: {}, stacktrace: {}", userId, throwable.getMessage(), throwable.getStackTrace()))
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, "Failed to retrieve user favourite restaurants. Error message: {}" + throwable.getMessage()))
                .fold(ErrorResponseHandler::respondError, ResponseEntity::ok);
    }

    @PostMapping
    public ResponseEntity<?> addToUserFavoriteRestaurants(@RequestBody FavoriteRestaurantRequestDto favoriteRestaurantRequestDto,
                                                          HttpServletRequest request) {

        String userId = request.getAttribute("uid").toString();
        return Try.of(() -> favoriteRestaurantsService.addToFavorite(userId, favoriteRestaurantRequestDto))
                .toEither()
                .peekLeft(throwable -> log.error("Error occurred while adding user favourite restaurant for userId: {}." +
                        " Error message: {}, stacktrace: {}", userId, throwable.getMessage(), throwable.getStackTrace()))
                .mapLeft(throwable -> throwable instanceof DataIntegrityViolationException ?
                        new Error(ErrorType.RESTAURANT_ALREADY_PRESENT_IN_FAVOURITES, "restaurant already exists in favourites for given user") :
                        new Error(ErrorType.INTERNAL_SERVER_ERROR, "Failed to retrieve user favourite restaurants. Error message: {}" + throwable.getMessage()))
                .flatMap(objects -> objects)
                .fold(ErrorResponseHandler::respondError, ResponseEntity::ok);
    }

    @DeleteMapping
    public ResponseEntity<?> removeUserFavoriteRestaurant(@RequestParam(value = "restaurantId", required = true) Long restaurantId,
                                                          HttpServletRequest request) {

        String userId = request.getAttribute("uid").toString();
        return Try.of(() -> favoriteRestaurantsService.removeFavoriteRestaurant(restaurantId, userId))
                .toEither()
                .peekLeft(throwable -> log.error("Error occurred while removing user favourite restaurant for userId: {}." +
                        " Error message: {}, stacktrace: {}", userId, throwable.getMessage(), throwable.getStackTrace()))
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, "Failed to retrieve user favourite restaurants. Error message: {}" + throwable.getMessage()))
                .fold(ErrorResponseHandler::respondError, ResponseEntity::ok);
    }
}
