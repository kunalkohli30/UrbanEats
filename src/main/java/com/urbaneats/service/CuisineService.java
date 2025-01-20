package com.urbaneats.service;

import com.urbaneats.model.Cuisine;
import com.urbaneats.repository.CuisineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuisineService {

    private final CuisineRepository cuisineRepository;

    public CuisineService(CuisineRepository cuisineRepository) {
        this.cuisineRepository = cuisineRepository;
    }

    public List<Cuisine> getCuisinesForRestaurant(Long restaurantId) {
        return cuisineRepository.findByRestaurantId(restaurantId);
    }
}
