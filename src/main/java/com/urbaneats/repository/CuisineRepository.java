package com.urbaneats.repository;

import com.urbaneats.model.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuisineRepository extends JpaRepository<Cuisine, Long> {

    public List<Cuisine> findByRestaurantId(Long restaurantId);
}
