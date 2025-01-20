package com.urbaneats.service;

import com.urbaneats.dto.FoodRequestDto;
import com.urbaneats.model.IngredientCategory;
import com.urbaneats.model.IngredientsItem;
import com.urbaneats.repository.IngredientCategoryRespository;
import com.urbaneats.repository.IngredientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientsService {
    private final IngredientsRepository ingredientsRepository;
    private final IngredientCategoryRespository ingredientCategoryRespository;

    @Autowired
    public IngredientsService(IngredientsRepository ingredientsRepository,
                              IngredientCategoryRespository ingredientCategoryRespository) {
        this.ingredientsRepository = ingredientsRepository;
        this.ingredientCategoryRespository = ingredientCategoryRespository;
    }

    public Optional<IngredientsItem> findById(Long id) {
        return ingredientsRepository.findById(id);
    }
    public IngredientsItem findByName(String name) {
        return ingredientsRepository.findByName(name);
    }

    public IngredientsItem saveIngredient(IngredientsItem ingredient) {
        return ingredientsRepository.save(ingredient);
    }

    public Optional<IngredientCategory> findIngredientCategoryByName(String name) {
        return ingredientCategoryRespository.findByName(name);
    }

    public Optional<IngredientCategory> findIngredientCategoryById(Long id){
        return ingredientCategoryRespository.findById(id);
    }

    public IngredientCategory saveIngredientCategory(IngredientCategory category){
        return ingredientCategoryRespository.save(category);
    }

    public List<IngredientsItem> getIngredientsList(List<IngredientsItem> ingredientItems) {
        return ingredientItems.stream().map(ingredient -> {
            IngredientCategory ingredientCategoryToBeSaved =
                    this.findIngredientCategoryByName(ingredient.getCategory().getName())
                            .orElseGet(() -> this.saveIngredientCategory(ingredient.getCategory()));

            ingredient.setCategory(ingredientCategoryToBeSaved);

            return Optional.ofNullable(this.findByName(ingredient.getName()))
                    .orElseGet(() -> this.saveIngredient(ingredient));

        }).toList();
    }
}
