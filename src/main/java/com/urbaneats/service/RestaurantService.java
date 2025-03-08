package com.urbaneats.service;

import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.restaurant.RestaurantRequestDto;
import com.urbaneats.dto.restaurant.RestaurantResponseDto;
import com.urbaneats.model.*;
import com.urbaneats.repository.AddressRepository;
import com.urbaneats.repository.RestaurantRepository;
import com.urbaneats.repository.UserRepository;
import com.urbaneats.repository.dataSetup.OffersDataSetupRepository;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Service
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OffersDataSetupRepository offersRepository;
    private final CuisineService cuisineService;
    private final GcloudStorageService gcloudStorageService;

    @Value("app.google.cloud.images.baseurl")
    private String imageBaseurl;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository,
                             AddressRepository addressRepository,
                             UserRepository userRepository,
                             OffersDataSetupRepository offersRepository,
                             CuisineService cuisineService,
                             GcloudStorageService gcloudStorageService) {
        this.restaurantRepository = restaurantRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.offersRepository = offersRepository;
        this.gcloudStorageService = gcloudStorageService;
        this.cuisineService = cuisineService;
    }

    public Optional<Restaurant> findById(Long id) {
        return restaurantRepository.findById(id);
    }
    public Restaurant createRestaurant(RestaurantRequestDto restaurantRequestDto, User user){
        Address savedAddress = addressRepository.save(restaurantRequestDto.getAddress());
        Restaurant restaurant = Restaurant.builder()
                .owner(user)
                .name(restaurantRequestDto.getName())
//                .address(restaurantRequestDto.getAddress())
//                .contactInformation(restaurantRequestDto.getContactInformation())
                .description(restaurantRequestDto.getDescription())
                .images(restaurantRequestDto.getImages())
                .openingHours(restaurantRequestDto.getOpeningHours())
                .registrationDate(LocalDateTime.now())
//                .address(savedAddress)
                .build();

        return restaurantRepository.save(restaurant);
    }

    public Either<Error, Restaurant> updateRestaurant(Long restaurantId, RestaurantRequestDto dto) {

        Optional<Restaurant> restaurants = restaurantRepository.findById(restaurantId)
                .map(response -> {
                    Restaurant updatedRestaurantObject = Restaurant.builder()
                            .id(response.getId())
                            .name(Objects.nonNull(dto.getName()) ? dto.getName() : response.getName())
                            .description(Objects.nonNull(dto.getDescription()) ? dto.getDescription() : response.getDescription())
                            .images(Objects.nonNull(dto.getImages()) ? dto.getImages() : response.getImages())
//                            .cuisineType(Objects.nonNull(dto.getCuisineType()) ? dto.getCuisineType() : response.getCuisineType())
                            .openingHours(Objects.nonNull(dto.getOpeningHours()) ? dto.getOpeningHours() : response.getOpeningHours())
                            .owner(Objects.nonNull(dto.getOwner()) ? dto.getOwner() : response.getOwner())
//                            .contactInformation(Objects.nonNull(dto.getContactInformation()) ? dto.getContactInformation() : response.getContactInformation())
                            .orders(response.getOrders())
//                            .address(Objects.nonNull(dto.getAddress()) ? dto.getAddress() : response.getAddress())
//                        .registrationDate(LocalDateTime.now())
                            .build();
                    return restaurantRepository.save(updatedRestaurantObject);
                });

        return restaurants.isPresent() ? Either.right(restaurants.get()) : Either.left(new Error(ErrorType.RESTAURANT_NOT_FOUND, String.format("Restaurant not found with given id - %s", restaurantId)));
    }

    public Either<Object, String> deleteRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .map(response -> {
                    restaurantRepository.delete(response);
                    return Either.right("DELETED_SUCCESSFULLY");
                })
                .orElseGet(() -> Either.left(new Error(ErrorType.RESTAURANT_NOT_FOUND, String.format("Restaurant not found with given id - %s", id))));
    }


    public List<Restaurant> searchRestaurant(String searchQuery) {
        return restaurantRepository.findBySearchQuery(searchQuery);
    }

    public Either<Error, Restaurant> getRestaurantByOwnerId(Long ownerId) {
        Optional<Restaurant> byOwnerId = restaurantRepository.findByOwnerId(ownerId);
        return byOwnerId.<Either<Error, Restaurant>>map(Either::right)
                .orElseGet(() -> Either.left(new Error(ErrorType.RESTAURANT_NOT_FOUND,
                        String.format("No restaurant found by the provided owner id - %s", ownerId))));
    }

    public Either<Error, List<RestaurantDto>> addToFavourites(Long restaurantId, Long userId){
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if(restaurant.isEmpty())
            return Either.left(new Error(ErrorType.RESTAURANT_NOT_FOUND, String.format("Restaurant not found with provided restaurant id - %s", restaurantId)));

        RestaurantDto restaurantDto = RestaurantDto.builder()
                .title(restaurant.get().getName())
                .description(restaurant.get().getDescription())
                .images(restaurant.get().getImages())
                .id(restaurant.get().getId())
                .build();

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty())
            return Either.left(new Error(ErrorType.USER_NOT_FOUND, String.format("User not found with provided user id - %s", userId)));

        if(user.get()
                .getFavourites()
                .stream()
                .noneMatch(favourite -> favourite.getId().equals(restaurantId))) {
            user.get().getFavourites().add(restaurantDto);
            userRepository.save(user.get());
            return Either.right(user.get().getFavourites());
        } else {
            return Either.left(new Error(ErrorType.RESTAURANT_ALREADY_PRESENT_IN_FAVOURITES, String.format("Restaurant already exists in favourites for user id: %s", userId)));
        }

    }

    public Either<Error, Restaurant> updateRestaurantStatus(Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id)
                .map(res -> {
                    res.setOpen(!res.isOpen());
                    return restaurantRepository.save(res);
                });

        return restaurant.isPresent() ? Either.right(restaurant.get()) :
                Either.left(new Error(ErrorType.RESTAURANT_NOT_FOUND,
                        String.format("No restaurant found with given id - %s", id)));

    }

    public List<Offers> fetchOffers() {
        Map<String, String> offersImageUrlMap = gcloudStorageService.getFoodImageUrlMap("offers/");
        return offersRepository.findAll()
                .stream()
                .map(offer -> {
                    offer.setOfferLogo(offersImageUrlMap.get(offer.getOfferLogo().split("\\.")[0]));
                    return offer;
                }).toList();
    }

    public List<RestaurantResponseDto> getAllRestaurant() {

        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        List<RestaurantResponseDto> response = allRestaurants.stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDto.class))
                .toList();

//        response.forEach(restaurantDto -> restaurantDto.setImageId(imageBaseurl + restaurantDto.getImageId()));

        response.forEach(restaurantDto -> {

            // Set image url for all the restaurants
            restaurantDto.setImageId(getRestaurantImageUrl(restaurantDto.getImageId(), restaurantDto.getName()));

            //        Set cuisines
            restaurantDto.setCuisines(
                    cuisineService.getCuisinesForRestaurant(restaurantDto.getId())
                            .stream()
                            .map(Cuisine::getCuisineName)
                            .toList()
            );
        });
        return response;
    }

    public RestaurantResponseDto getRestaurantData(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if(restaurant.isEmpty()) {
            log.error("Wrong restaurant data requested, unable to retrieve from db. Restaurant id: {}", restaurantId);
            return null;
        }

        RestaurantResponseDto responseDto = modelMapper.map(restaurant, RestaurantResponseDto.class);
        List<Cuisine> cuisines = cuisineService.getCuisinesForRestaurant(restaurant.get().getId());
        responseDto.setCuisines(cuisines.stream().map(Cuisine::getCuisineName).toList());
        responseDto.setImageId(getRestaurantImageUrl(restaurant.get().getImageId(), restaurant.get().getName()));
        return responseDto;
    }

    public Restaurant getRestaurantEntity(Long restaurantId) {
        return restaurantRepository.findById(restaurantId).orElse(null);
    }

    public  String getRestaurantImageUrl(String imageId, String restaurantName) {

        if(imageId == null || StringUtils.isEmpty(imageId))
            return StringUtils.EMPTY;

        Map<String, String> restaurantImageUrlMap = gcloudStorageService.getFoodImageUrlMap("restaurant_images/");

        String imageIdFromDb = gcloudStorageService.getImageIdForFetchingImageUrl(imageId);
        if(imageIdFromDb.equalsIgnoreCase(StringUtils.EMPTY))
            log.error("Failed to get the final imageId for restaurant : {}, imageId: {}", restaurantName, imageId);

        return restaurantImageUrlMap.get(imageIdFromDb);
    }
}
