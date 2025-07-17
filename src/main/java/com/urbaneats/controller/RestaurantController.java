package com.urbaneats.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.urbaneats.config.SecretProperties;
import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.model.Restaurant;
import com.urbaneats.model.User;
import com.urbaneats.service.RestaurantService;
import com.urbaneats.service.UserService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/public/restaurant")
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final UserService userService;

    @Value("${DB_PASS:PASSWORD_NOT_FOUND}")
    private String dbPassword;

//    @Value("${FIREBASE_API_KEY}")
//    String firebasekey;

    @Autowired
    private SecretProperties secretProperties;

    @Autowired
    public RestaurantController(RestaurantService restaurantService, UserService userService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRestaurant(HttpServletRequest request,
                                              @RequestParam("keyword") String keyword,
                                              @RequestHeader("Authorization") String jwt) {

//        User user = userService.findUserByJwtToken(jwt);
        List<Restaurant> restaurants = restaurantService.searchRestaurant(keyword);
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }



//    getrestaurantByOwnerId implemented by yt guy, skipped because it was included in restaurantAdmin controller.
//    public ResponseEntity<?> getRes(@RequestHeader("Authorization") String jwt) {
//
////        User user = userService.findUserByJwtToken(jwt);
//        List<Restaurant> restaurants = restaurantService.getAllRestaurant();
//        return new ResponseEntity<>(restaurants, HttpStatus.OK);
//    }

//    Returns list of updated favouorites list corresponding to the user fetched from jwt token
    @PutMapping("/{restaurantId}/add-favourites")
    public ResponseEntity<?> addToFavourites(@PathVariable("restaurantId") Long restaurantId,
                                             @RequestHeader("Authorization") String jwt) {
        Either<Error, User> user = userService.findUserByJwtToken(jwt);
        if(user.isLeft())
            return ErrorResponseHandler.respondError(user.getLeft());

        return restaurantService.addToFavourites(restaurantId, user.get().getId())
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.CREATED));

    }

    @GetMapping("/offers")
    public ResponseEntity<?> getOffers() {
        log.info("--------- Secrets - DB_HOST: " + dbPassword + " " + secretProperties.getFirebaseApiKey());
        return Try.of(restaurantService::fetchOffers)
                .toEither()
                .peekLeft(throwable -> log.error("exception occurred while fetching offers. Message:{}, stacktrace: {}",
                        throwable.getMessage(), throwable.getStackTrace()))
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR,
                        "OOPS! Some internal error occurred. Sorry for the inconvenience!!!"))
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllRestaurants(HttpServletRequest request) throws FirebaseAuthException {

        return Try.of(restaurantService::getAllRestaurant)
                .toEither()
                .peekLeft(throwable -> log.error("exception occurred in getAll restaurants api. message:{}, stacktrace:{}", throwable.getMessage(), throwable.getStackTrace()))
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, throwable.getMessage()))
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @GetMapping("{restaurantId}")
    public ResponseEntity<?> getRestaurantData(@PathVariable Long restaurantId,
                                               HttpServletRequest request) throws FirebaseAuthException {

        return Try.of(() -> restaurantService.getRestaurantData(restaurantId))
                .toEither()
                .peekLeft(throwable -> log.error("exception occurred in restaurant by id api . message:{}, stacktrace:{}", throwable.getMessage(), throwable.getStackTrace()))
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, throwable.getMessage()))
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }
}
