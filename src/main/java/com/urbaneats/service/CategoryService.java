package com.urbaneats.service;

import com.urbaneats.dto.error.Error;
import com.urbaneats.model.Category;
import com.urbaneats.repository.CategoryRepository;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FoodService foodService;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, FoodService foodService) {
        this.categoryRepository = categoryRepository;
        this.foodService = foodService;
    }

    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category){
        return categoryRepository.save(category);
    }

    public Either<Error, List<Category>> findByRestaurantId(Long restaurantId) {
//        Either<Error, List<Food>> restaurantMenu = foodService.getRestaurantMenu(restaurantId);
//        if(restaurantMenu.isLeft())
//            return Either.left(restaurantMenu.getLeft());
//
//        List<Category> categories = restaurantMenu.get()
//                .stream()
//                .map(Food::getFoodCategory)
//                .toList();
//            return Either.right(categories);

        return null;
    }

//    findCategoryByRestaurantId
//    findById
//
}
