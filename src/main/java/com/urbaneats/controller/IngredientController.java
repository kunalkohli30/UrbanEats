package com.urbaneats.controller;

import com.urbaneats.dto.IngredientRequestDto;
import com.urbaneats.model.IngredientCategory;
import com.urbaneats.model.IngredientsItem;
import com.urbaneats.service.IngredientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientsService ingredientsService;

    @Autowired
    public IngredientController(IngredientsService ingredientsService) {
        this.ingredientsService = ingredientsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(Long id){
        return new ResponseEntity<>(ingredientsService.findById(id), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<?> createIngredient(@RequestBody IngredientsItem ingredient){
        return new ResponseEntity<>(ingredientsService.saveIngredient(ingredient), HttpStatus.CREATED);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> findIngredientCategoryById(@PathVariable("id") Long categoryId){
        return new ResponseEntity<>(ingredientsService.findIngredientCategoryById(categoryId), HttpStatus.OK);
    }

    @PostMapping("/category")
    public ResponseEntity<?> createIngredientCategory(@RequestBody IngredientCategory ingredientCategory){
        return new ResponseEntity<>(ingredientsService.saveIngredientCategory(ingredientCategory), HttpStatus.CREATED);
    }
}
