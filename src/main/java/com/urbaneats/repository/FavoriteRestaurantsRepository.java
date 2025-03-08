package com.urbaneats.repository;

import com.urbaneats.model.FavoriteRestaurants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRestaurantsRepository extends JpaRepository<FavoriteRestaurants, Long> {

    public List<FavoriteRestaurants> findByUserId(String userId);
    public List<FavoriteRestaurants> findByUserIdAndRestaurantId(String userId, Long restaurantId);

}
