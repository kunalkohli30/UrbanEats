package com.urbaneats.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.urbaneats.dto.*;
import com.urbaneats.dto.Error;
import com.urbaneats.dto.dataSetup.FoodDataEntry;
import com.urbaneats.model.Category;
import com.urbaneats.model.Food;
import com.urbaneats.model.FoodTypes;
import com.urbaneats.model.Restaurant;
import com.urbaneats.repository.CategoryRepository;
import com.urbaneats.repository.FoodRepository;
import com.urbaneats.repository.FoodTypesRepository;
import com.urbaneats.repository.RestaurantRepository;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
public class FoodService {

    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodTypesRepository foodTypesRepository;
    private final GcloudStorageService gcloudStorageService;

    @Autowired
    public FoodService(FoodRepository foodRepository, CategoryRepository categoryRepository,
                       RestaurantRepository restaurantRepository, FoodTypesRepository foodTypesRepository, GcloudStorageService gcloudStorageService) {
        this.foodRepository = foodRepository;
        this.categoryRepository = categoryRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodTypesRepository = foodTypesRepository;
        this.gcloudStorageService = gcloudStorageService;
    }

    public Food createFoodItem(FoodRequestDto foodRequestDto, Category category, Restaurant restaurant) {
        ModelMapper modelMapper = new ModelMapper();
        Food foodModel = modelMapper.map(foodRequestDto, Food.class);

        foodModel.setFoodCategory(category);
        foodModel.setRestaurant(restaurant);
        if(Objects.isNull(foodModel.getCreationDate()) )
            foodModel.setCreationDate(LocalDate.now());

        return foodRepository.save(foodModel);
    }

    public Option<Object> deleteFoodItem(Long foodId) {
        Optional<Food> food = foodRepository.findById(foodId);
        food.get().setRestaurant(null);
        foodRepository.save(food.get());
        return Option.none();
    }

    public List<Food> getRestaurantsFood(Long restaurantId,
                                         boolean isVegetarian,
                                         boolean isSeasonal,
                                         String foodCategory) {

        List<Food> foodList = foodRepository.findByRestaurantId(restaurantId);
        if(Objects.isNull(foodList) || foodList.isEmpty())
            return null;

        return foodList.stream()
                .filter(food -> isVegetarian == food.isVegetarian())
                .filter(food-> isSeasonal == food.isSeasonal())
                .filter(food -> isNotBlank(foodCategory) && food.getFoodCategory().getName().equals(foodCategory))
                .toList();
    }

    public Either<Error, List<RestaurantMenuResponseDto>> getRestaurantMenu(Long restaurantId) {

        Optional<List<Food>> menu = Optional.ofNullable(foodRepository.findByRestaurantId(restaurantId));
        Map<String, RestaurantMenuResponseDto> responseMap = new HashMap<String, RestaurantMenuResponseDto>();

        Map<String, String> imageUrlMap = gcloudStorageService.getFoodImageUrlMap("foodItems/");

        if(menu.isEmpty())
            return Either.left(new Error(ErrorType.NOT_FOUND,
                    String.format("No food items found linked to restaurant with id: %s",  restaurantId)));

        menu.get().forEach(food -> {
            String categoryName = food.getFoodCategory().getName();

            String imageIdFromDb = gcloudStorageService.getImageIdForFetchingImageUrl(food.getImageId());
            if(imageIdFromDb.equalsIgnoreCase(StringUtils.EMPTY))
                log.error("Failed to get the final imageId for food item: {}, imageId: {}", food.getName(), food.getImageId());

            if(responseMap.containsKey(categoryName)) {
                responseMap.get(categoryName)
                        .getFoodItems()
                        .add(mapFoodEntityToFoodItemsResponseDto.apply(food, imageUrlMap.get(imageIdFromDb)));
            }
            else {
                List<FoodItemsResponseDto> foodItems = new ArrayList<>();
                foodItems.add(mapFoodEntityToFoodItemsResponseDto.apply(food, imageUrlMap.get(imageIdFromDb)));
                responseMap.put(categoryName,new RestaurantMenuResponseDto(categoryName, foodItems));
            }
        });


            return Either.right(responseMap.values().stream().toList());

    }

    BiFunction<Food, String, FoodItemsResponseDto> mapFoodEntityToFoodItemsResponseDto = (food, imageUrl) -> FoodItemsResponseDto.builder()
            .id(food.getId())
            .name(food.getName())
            .description(food.getDescription())
            .price(food.getPrice())
            .available(food.isAvailable())
            .isVegetarian(food.isVegetarian())
            .isSeasonal(food.isSeasonal())
            .creationDate(food.getCreationDate())
            .imageId(imageUrl)
            .restaurantId(food.getRestaurant().getId())
            .foodCategory(food.getFoodCategory().getName())
            .build();

    public Either<Error, Food> findById(Long foodId) {
        Optional<Food> foods = foodRepository.findById(foodId);

        return foods.<Either<Error, Food>>map(Either::right).orElseGet(() -> Either.left(new Error(ErrorType.NOT_FOUND,
                "No Food item found with provided id")));
    }

    public List<FoodTypes> getFoodTypes() {
        return foodTypesRepository.findAll();
    }

    public List<FoodItemsResponseDto> searchFoodItems(String keyword) {
        List<Food> foods = foodRepository.searchFoodItem(keyword);
        Map<String, String> imageUrlMap = gcloudStorageService.getFoodImageUrlMap("foodItems/");

        return foods.stream().map(foodItem -> {
            String imageIdFromDb = gcloudStorageService.getImageIdForFetchingImageUrl(foodItem.getImageId());
            if(imageIdFromDb.equalsIgnoreCase(StringUtils.EMPTY))
                log.error("Failed to get the final imageId for food item: {}, imageId: {}", foodItem.getName(), foodItem.getImageId());
            return mapFoodEntityToFoodItemsResponseDto.apply(foodItem, imageUrlMap.get(imageIdFromDb));
        }).toList();
    }

    public Either<Error, Food> toggleAvailabilityStatus(Long foodId) {
        Optional<Food> food = foodRepository.findById(foodId);

        if(food.isEmpty()) return Either.left(new Error(ErrorType.NOT_FOUND, "No food item found with provided id"));

        food.get().setAvailable(!food.get().isAvailable());
        return Either.right(foodRepository.save(food.get()));
    }

    public List<Category> createCategory(List<Category> category) {
        List<Category> unsavedCategories = category.stream()
                .filter(cat -> categoryRepository.findByName(cat.getName()).isEmpty())
                .toList();
        return categoryRepository.saveAll(unsavedCategories);
    }

    public void bulkInsert(List<FoodDataEntry> foodDataEntries) {

        List<Food> foodList = new ArrayList<>();
        foodDataEntries.forEach(foodEntry -> {

            try {
                Category category = Category.builder().name(foodEntry.getCategory()).build();
                Optional<Category> categoryOptional = categoryRepository.findByName(foodEntry.getCategory());

                if (categoryOptional.isEmpty())
                    category = categoryRepository.save(category);
                else
                    category = categoryOptional.get();

                Optional<Restaurant> restaurant = restaurantRepository.findById(foodEntry.getRestaurantId());

                if(foodRepository.findByNameAndRestaurant(foodEntry.getName(), restaurant.get()).isEmpty()) {

                    Food food = Food.builder()
                            .name(foodEntry.getName())
                            .price(foodEntry.getPrice())
                            .description(foodEntry.getDescription())
                            .imageId(foodEntry.getImageId())
                            .isVegetarian(foodEntry.isVegetarian())
                            .foodCategory(category)
                            .restaurant(restaurant.get())
                            .creationDate(LocalDate.now())
                            .available(true)
                            .isSeasonal(false)
                            .build();

                    foodList.add(food);
                }
            } catch (Exception e) {
                log.error("failed for entry: {}", foodEntry);
                log.error("Exception", e);
            }
        });

        List<Food> saved = foodRepository.saveAll(foodList);
    }


    public void updateVeg(List<FoodDataEntry> foodDataEntries) {
        List<Food> foodList = foodDataEntries.stream().flatMap(f -> {
            return foodRepository.findByName(f.getName())
                    .stream().peek(m -> {
                        m.setVegetarian(f.isVegetarian());
                        m.setPrice(f.getPrice());
                    });
        }).toList();

        List<Food> foods = foodRepository.saveAll(foodList);

    }
}
