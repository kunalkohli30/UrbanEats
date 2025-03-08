package com.urbaneats.service;

import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.restaurant.FavoriteRestaurantRequestDto;
import com.urbaneats.dto.restaurant.FavoriteRestaurantsResponseDto;
import com.urbaneats.dto.restaurant.RestaurantResponseDto;
import com.urbaneats.model.Cuisine;
import com.urbaneats.model.FavoriteRestaurants;
import com.urbaneats.model.Restaurant;
import com.urbaneats.repository.FavoriteRestaurantsRepository;
import io.vavr.control.Either;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class FavoriteRestaurantsService {

    private final FavoriteRestaurantsRepository favoriteRestaurantsRepository;
    private final RestaurantService restaurantService;
    private final CuisineService cuisineService;

    public FavoriteRestaurantsService(FavoriteRestaurantsRepository favoriteRestaurantsRepository, RestaurantService restaurantService, CuisineService cuisineService) {
        this.favoriteRestaurantsRepository = favoriteRestaurantsRepository;
        this.restaurantService = restaurantService;
        this.cuisineService = cuisineService;
    }


    public List<FavoriteRestaurantsResponseDto> getUserFavoriteRestaurants(String userId) {
        return favoriteRestaurantsRepository.findByUserId(userId)
                .stream().map(favoriteRestaurant -> {
                    Restaurant restaurant = favoriteRestaurant.getRestaurant();
                    List<Cuisine> cuisines = cuisineService.getCuisinesForRestaurant(restaurant.getId());
                    return FavoriteRestaurantsResponseDto.builder()
                            .id(favoriteRestaurant.getId())
                            .restaurantId(restaurant.getId())
                            .name(restaurant.getName())
                            .locality(restaurant.getLocality())
                            .areaName(restaurant.getAreaName())
                            .city(restaurant.getCity())
                            .costForTwo(restaurant.getCostForTwo())
                            .avgRatingString(restaurant.getAvgRatingString())
                            .isOpen(restaurant.isOpen())
                            .discountInfo(restaurant.getDiscountInfo())
                            .imageId(restaurantService.getRestaurantImageUrl(restaurant.getImageId(), restaurant.getName()))
                            .cuisines(cuisines.stream().map(Cuisine::getCuisineName).toList())
                            .addedToFavoritesTimestamp(favoriteRestaurant.getCreatedAt())
                            .notes(favoriteRestaurant.getNotes())
                            .build();
                }).toList();
    }

    @PostMapping
    public Either<Error, String> addToFavorite(String userId, FavoriteRestaurantRequestDto favoriteRestaurantRequestDto) {

        Restaurant restaurantEntity = restaurantService.getRestaurantEntity(favoriteRestaurantRequestDto.getRestaurantId());
        if (Objects.isNull(restaurantEntity))
            return Either.left(new Error(ErrorType.RESTAURANT_DOES_NOT_EXIST, String.format("Restaurant with id %s does not exist", favoriteRestaurantRequestDto.getRestaurantId())));

        FavoriteRestaurants favoriteRestaurant = FavoriteRestaurants.builder()
                .restaurant(restaurantEntity)
                .userId(userId)
                .notes(favoriteRestaurantRequestDto.getNotes())
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRestaurantsRepository.save(favoriteRestaurant);         // DataIntegrityViolationException would occur if the restaurantId is already added to favorites for userId
        return Either.right("ADDED_TO_FAVORITES");
    }

    @DeleteMapping
    public List<FavoriteRestaurantsResponseDto> removeFavoriteRestaurant(Long restaurantId, String userId) {
        List<FavoriteRestaurants> restaurant = favoriteRestaurantsRepository.findByUserIdAndRestaurantId(userId, restaurantId);
        favoriteRestaurantsRepository.deleteAll(restaurant);
        return getUserFavoriteRestaurants(userId);
    }
}
