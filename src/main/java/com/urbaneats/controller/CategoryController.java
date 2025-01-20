package com.urbaneats.controller;

import com.urbaneats.dto.Error;
import com.urbaneats.dto.ErrorType;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.model.Category;
import com.urbaneats.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/getCategoryByRestaurantId")
    public ResponseEntity<?> getCategoryByRestaurantId(@RequestParam Long restaurantId) {
        return categoryService.findByRestaurantId(restaurantId)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestParam String categoryName) {
        return new ResponseEntity<>(categoryService.saveCategory(
                Category.builder()
                        .name(categoryName)
                        .build()
        ), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@RequestParam Long id) {
        Optional<Category> categoryById = categoryService.findCategoryById(id);
        return categoryById.isPresent() ?
                new ResponseEntity<>(categoryById.get(), HttpStatus.OK) :
                ErrorResponseHandler.respondError(new Error(ErrorType.NOT_FOUND,
                        "No category found with provided category id: "+ id));

    }
}
