package com.urbaneats.repository;

import com.urbaneats.model.FoodTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodTypesRepository extends JpaRepository<FoodTypes, Long> {
}
