package com.urbaneats.repository;

import com.urbaneats.model.Category;
import com.urbaneats.model.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientCategoryRespository extends JpaRepository<IngredientCategory, Long> {

    Optional<IngredientCategory> findByName(String name);

}
