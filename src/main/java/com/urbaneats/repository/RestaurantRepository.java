package com.urbaneats.repository;

import com.urbaneats.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query(value = """
                SELECT * from restaurant
                WHERE lower(name) LIKE lower(concat('%', :query, '%'))
                    OR lower(cuisineType) LIKE lower(concat('%', :query, '%'))
            """, nativeQuery = true)
    public List<Restaurant> findBySearchQuery(String query);

    public Optional<Restaurant> findByOwnerId(Long ownerId);

    public List<Restaurant> findByName(String name);
}
