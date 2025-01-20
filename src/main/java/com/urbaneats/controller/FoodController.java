package com.urbaneats.controller;

import com.urbaneats.dto.FoodItemsResponseDto;
import com.urbaneats.dto.dataSetup.FoodDataEntry;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.model.Category;
import com.urbaneats.model.Food;
import com.urbaneats.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/food")
public class FoodController {

    private final FoodService foodService;

    @Autowired
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> getFoodItem(@RequestParam("keyword") String keyword) {

        List<FoodItemsResponseDto> foods = foodService.searchFoodItems(keyword);
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{id}/filtered")
    public ResponseEntity<?> getRestaurantMenu(@PathVariable("id") Long restaurantId,
                                               @RequestParam final Boolean vegeterian,
                                               @RequestParam final Boolean seasonal,
                                               @RequestParam(required = false) final String category) {
        List<Food> restaurantsFood = foodService.getRestaurantsFood(restaurantId, vegeterian, seasonal, category);
        return new ResponseEntity<>(restaurantsFood, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<?> getRestaurantMenu(@PathVariable("id") Long restaurantId) {
        return foodService.getRestaurantMenu(restaurantId)
                .fold(ErrorResponseHandler::respondError,
                        menu -> new ResponseEntity<>(menu, HttpStatus.OK));
    }

    @GetMapping("/types")
    public ResponseEntity<?> getFoodTypes() {
        return ResponseEntity.ok().body(foodService.getFoodTypes());
    }


    @PostMapping("/category")
    public ResponseEntity<?> createCategory(@RequestBody List<Category> category) {
        return new ResponseEntity<>(foodService.createCategory(category), HttpStatus.CREATED);

    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulkInsert(@RequestBody List<FoodDataEntry> foodDataEntries) {
        foodService.bulkInsert(foodDataEntries);
//        foodService.updateVeg(foodDataEntries);
        return null;
    }
}
