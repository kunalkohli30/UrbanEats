package com.urbaneats.controller;

import com.urbaneats.dto.Error;
import com.urbaneats.dto.RestaurantRequestDto;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.model.User;
import com.urbaneats.service.RestaurantService;
import com.urbaneats.service.UserService;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/admin/restaurants")
public class RestaurantAdminController {

    private final RestaurantService restaurantService;
    private final UserService userService;

    @Autowired
    public RestaurantAdminController(RestaurantService restaurantService, UserService userService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

//    Tested
    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody RestaurantRequestDto restaurantRequestDto,
                                              @RequestHeader("Authorization") String jwt) {

        return userService.findUserByJwtToken(jwt)
                .map(user -> restaurantService.createRestaurant(restaurantRequestDto, user))
                .fold(error -> new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED),
                        response -> new ResponseEntity<>(response, HttpStatus.CREATED));
    }

//    Provide all the values which have to be updated, keep null for fields which are not required to update.
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRestaurant(@RequestBody RestaurantRequestDto restaurantRequestDto,
                                              @PathVariable Long id,
                                              @RequestHeader("Authorization") String jwt) {

        return userService.findUserByJwtToken(jwt)
                .map(user -> restaurantService.updateRestaurant(id, restaurantRequestDto))
                .flatMap(objects -> objects)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable Long id,
                                              @RequestHeader("Authorization") String jwt) {

//        find user method can be removed if we do not need to save the updatedByUser details in future.
        return userService.findUserByJwtToken(jwt)
                .map(user -> restaurantService.deleteRestaurant(id))
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateRestaurantStatus(@PathVariable Long id,
                                                    @RequestHeader("Authorization") String jwt) {

//        find user method can be removed if we do not need to save the updatedByUser details in future.
        return userService.findUserByJwtToken(jwt)
                .map(user -> restaurantService.updateRestaurantStatus(id))
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    @GetMapping("findByOwner")
    public ResponseEntity<?> findRestaurantByOwner(@RequestParam ("ownerid") Long ownerid) {
        return restaurantService.getRestaurantByOwnerId(ownerid)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }
}
