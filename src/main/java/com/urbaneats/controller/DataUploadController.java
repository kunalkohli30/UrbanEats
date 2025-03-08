package com.urbaneats.controller;

import com.urbaneats.dto.restaurant.RestaurantDataInsertDto;
import com.urbaneats.model.Cuisine;
import com.urbaneats.model.Offers;
import com.urbaneats.model.Restaurant;
import com.urbaneats.repository.CuisineRepository;
import com.urbaneats.repository.RestaurantRepository;
import com.urbaneats.repository.dataSetup.OffersDataSetupRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/upload/data")
public class DataUploadController {

    private final RestaurantRepository restaurantRepository;
    private final CuisineRepository cuisineRepository;
    private final OffersDataSetupRepository offersDataSetupRepository;

    @Autowired
    public DataUploadController(RestaurantRepository restaurantRepository,
                                CuisineRepository cuisineRepository,
                                OffersDataSetupRepository offersDataSetupRepository) {
        this.restaurantRepository = restaurantRepository;
        this.cuisineRepository = cuisineRepository;
        this.offersDataSetupRepository = offersDataSetupRepository;
    }

    @PostMapping("/restaurant")
    public ResponseEntity<?> uploadRestaurantData(@RequestBody List<RestaurantDataInsertDto> restaurant) {
        List<Restaurant> list = restaurant.stream().map(restaurantData -> Restaurant.builder()
                        .name(restaurantData.getName())
                        .email(restaurantData.getEmail())
//                        .description(restaurantData.getDescription())
                        .locality(restaurantData.getLocality())
                        .areaName(restaurantData.getAreaName())
                        .costForTwo(restaurantData.getCostForTwo())
                        .avgRatingString(restaurantData.getAvgRatingString())
                        .totalRatingsString(restaurantData.getTotalRatingsString())
                        .isOpen(restaurantData.isOpen())
                        .discountInfo(restaurantData.getDiscountInfo().getHeader() + " " + restaurantData.getDiscountInfo().getSubHeader() + " " + restaurantData.getDiscountInfo().getDiscountTag())
                        .imageId(restaurantData.getImageId())
                        .openingHours("09:00")
                        .registrationDate(LocalDateTime.now())
                        .instagram(restaurantData.getName())
                        .build()
        ).toList();
        List<Restaurant> restaurants = restaurantRepository.saveAll(list);

        List<Cuisine> cuisineList = restaurant.stream()
                .flatMap(res -> res.getCuisines()
                        .stream()
                        .map(cuis -> Cuisine
                                .builder()
                                .cuisineName(cuis)
                                .restaurant(restaurantRepository.findByName(res.getName()).get(0))
                                .build()
                        )
                )
                .toList();
        List<Cuisine> savedAll = cuisineRepository.saveAll(cuisineList);
        return new ResponseEntity<>(savedAll, HttpStatus.OK);
    }

    @PostMapping("/offers")
    public ResponseEntity<?> saveOffers(@RequestBody List<Offers> offers, HttpServletRequest request) {
        offersDataSetupRepository.saveAll(offers);
                return null;
    }
}
