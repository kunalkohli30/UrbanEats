package com.urbaneats.controller;

import com.urbaneats.dto.Error;
import com.urbaneats.dto.ErrorType;
import com.urbaneats.dto.FoodRequestDto;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.model.*;
import com.urbaneats.service.*;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("api/admin/food")
public class AdminFoodController {

    private final FoodService foodService;
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final CategoryService categoryService;
    private final IngredientsService ingredientsService;


    @Autowired
    public AdminFoodController(FoodService foodService,
                               UserService userService,
                               RestaurantService restaurantService,
                               CategoryService categoryService,
                               IngredientsService ingredientsService) {
        this.foodService = foodService;
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.categoryService = categoryService;
        this.ingredientsService = ingredientsService;
    }

    @PostMapping
    public ResponseEntity<?> createfoodItem(@RequestBody FoodRequestDto foodRequestDto,
                                            @RequestHeader("Authorization") String jwtToken) {

        Optional<Restaurant> restaurant = restaurantService.findById(foodRequestDto.getRestaurantId());
        if(restaurant.isEmpty())
            return ErrorResponseHandler.respondError(new Error(ErrorType.NOT_FOUND,
                    "Restaurant not found with provided id: " + foodRequestDto.getRestaurantId())
            );

        List<IngredientsItem> ingredientsList = ingredientsService.getIngredientsList(foodRequestDto.getIngredients());
        foodRequestDto.setIngredients(ingredientsList);

        Category categoryToBeSaved = categoryService.findCategoryByName(foodRequestDto.getFoodCategory().getName())
                .orElseGet(() -> categoryService.saveCategory(foodRequestDto.getFoodCategory()));

        Food foodItem = foodService.createFoodItem(foodRequestDto, categoryToBeSaved, restaurant.get());

        return new ResponseEntity<>(foodItem, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteFoodItem(@PathVariable Long id) {
        return Try.of( () ->  foodService.deleteFoodItem(id))
                .toEither()
                .map(respo -> String.format("Food item id: %s is not longer associated with any restaurant", id))
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, "Failed to delete food item"))
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PutMapping("{id}/toggleAvailability")
    public ResponseEntity<?> updateFoodAvailability(@PathVariable Long id) {
        return Try.of( () ->  foodService.toggleAvailabilityStatus(id))
                .toEither()
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, "Failed to delete food item"))
                .flatMap(objects -> objects)
                .map(respo -> String.format("Food item: %s, id: %s and restaurant id: %s is %s now",
                        respo.getName(),
                        id,
                        Optional.ofNullable(respo.getRestaurant())
                                .map(Restaurant::getId)
                                .or(Optional::empty),
                        respo.isAvailable() ? "Available" : "Not Available"))
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }
}
