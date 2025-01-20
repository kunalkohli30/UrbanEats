package com.urbaneats.repository;

import com.urbaneats.model.Food;
import com.urbaneats.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByRestaurantId(long restaurantId);

    @Query("""
            SELECT f FROM Food f
            WHERE f.name LIKE %:keyword% OR f.description LIKE %:keyword%
        """)
    List<Food> searchFoodItem(String keyword);

    List<Food> findByNameAndRestaurant(String name, Restaurant restaurant);
    List<Food> findByName(String name);

}
