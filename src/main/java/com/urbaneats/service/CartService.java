package com.urbaneats.service;

import com.urbaneats.dto.cartResponse.*;
import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.model.Cart;
import com.urbaneats.model.CartItem;
import com.urbaneats.model.Food;
import com.urbaneats.model.Restaurant;
import com.urbaneats.repository.CartItemRepository;
import com.urbaneats.repository.CartRepository;
import com.urbaneats.repository.RestaurantRepository;
import io.vavr.control.Either;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.urbaneats.commons.Constants.cartRestaurantIdMismatchErrorMessage;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodService foodService;
    private final RestaurantService restaurantService;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       FoodService foodService,
                       RestaurantService restaurantService, RestaurantRepository restaurantRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.foodService = foodService;
        this.restaurantService = restaurantService;
        this.restaurantRepository = restaurantRepository;
    }

    public CartResponseDto getCartData(String userId) {

        Optional<List<Cart>> userCart = cartRepository.findByCustomerId(userId);
        if(userCart.isEmpty() || userCart.get().isEmpty()) {
            Cart cart = Cart.builder().customerId(userId).total(0).build();
            cartRepository.save(cart);
            return null;
        }

        Cart cart = userCart.get().get(0);
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId());
        List<CartItemResponseDto> cartItemsResponse = cartItems.stream().map(cartItem -> CartItemResponseDto.builder()
                .foodId(cartItem.getFood().getId())
                .foodItemName(cartItem.getFood().getName())
                .isVeg(cartItem.getFood().isVegetarian())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getFood().getPrice())
                .build()
        ).toList();

        return CartResponseDto.builder()
                .cartId(cart.getId())
                .cartItems(cartItemsResponse)
                .restaurantId(Optional.ofNullable(cart.getRestaurant()).map(Restaurant::getId).orElse(null))
                .restaurantName(Optional.ofNullable(cart.getRestaurant()).map(Restaurant::getName).orElse(null))
                .restaurantAreaName(Optional.ofNullable(cart.getRestaurant()).map(Restaurant::getAreaName).orElse(null))
                .restaurantImageUrl(restaurantService.getRestaurantImageUrl(Optional.ofNullable(cart.getRestaurant()).map(Restaurant::getImageId).orElse(null),
                        Optional.ofNullable(cart.getRestaurant()).map(Restaurant::getName).orElse(null)))
                .build();
    }


    public Optional<Cart> findCartById(Long id) {
        return cartRepository.findById(id);
    }

    public Either<Error, com.urbaneats.dto.CartItemResponseDto> addItemToCart(CartItemRequestDto cartItemRequestDto, String userId) {

        Optional<List<Cart>> cartList = cartRepository.findByCustomerId(userId);
        if (cartList.isEmpty() || cartList.get().isEmpty())
            return Either.left(new Error(ErrorType.NOT_FOUND, "CART_NOT_FOUND"));

        Either<Error, Food> foodItem = foodService.findById(cartItemRequestDto.getFoodId());
        if (foodItem.isEmpty()) return Either.left(new Error(ErrorType.NOT_FOUND, "FOOD_ITEM_NOT_FOUND"));

        Cart cart = cartList.get().get(0);
        Long restaurantId = foodItem.get().getRestaurant().getId();

        Restaurant restaurantEntity = null;
        if (Optional.ofNullable(cart.getRestaurant()).isEmpty()) {          // no restaurantId present in cart

            restaurantEntity = restaurantService.getRestaurantEntity(restaurantId);
            if (restaurantEntity == null)
                return Either.left(new Error(ErrorType.RESTAURANT_NOT_FOUND, "Invalid restaurant id provided"));

            cart.setRestaurant(restaurantEntity);
            cartRepository.save(cart);                                      // if no restaurant exists in cart, save the current one in it
        } else {                                                            // restaurant id of foodItem and restaurantId already present in cart does  not match, then give error
            if (!Objects.equals(cart.getRestaurant().getId(), restaurantId))
                return Either.left(new Error(ErrorType.CART_RESTAURANT_ID_MISMATCH, cartRestaurantIdMismatchErrorMessage));
        }

        // checking if the same food item is present in this user's cart, if no then save a new cartItem, if yes then update quantity of existing one
        List<CartItem> existingSameFoodItems = cartItemRepository.findByFoodIdAndCartId(cartItemRequestDto.getFoodId(), cart.getId());
        if(cartItemRequestDto.getOperation().equalsIgnoreCase("SUBTRACT") && existingSameFoodItems.isEmpty())               //Edge case, if cart item does not exist or is already removed, then dont do further processing
            return Either.right(null);

        CartItem cartItemToBeSaved = null;
        CartItem cartItemResponseObj = null;
        if(cartItemRequestDto.getOperation().equalsIgnoreCase("ADD")) {
            if (Objects.isNull(existingSameFoodItems) || existingSameFoodItems.isEmpty()) {
                cartItemToBeSaved = CartItem.builder()
                        .quantity(1)
                        .totalPrice(foodItem.get().getPrice())
                        .cart(cart)
                        .food(foodItem.get())
                        .restaurant(cart.getRestaurant())
                        .build();
            } else {
                cartItemToBeSaved = existingSameFoodItems.get(0);
                cartItemToBeSaved.setQuantity(cartItemToBeSaved.getQuantity() + 1);
                cartItemToBeSaved.setTotalPrice(foodItem.get().getPrice() * (cartItemToBeSaved.getQuantity() + 1));
            }
            cartItemResponseObj = cartItemRepository.save(cartItemToBeSaved);
        }
        else {                                                  //remove items from cart
            cartItemToBeSaved = existingSameFoodItems.get(0);
            if (cartItemToBeSaved.getQuantity() > 1) {
                cartItemToBeSaved.setQuantity(cartItemToBeSaved.getQuantity() - 1);
                cartItemToBeSaved.setTotalPrice(foodItem.get().getPrice() * (cartItemToBeSaved.getQuantity() - 1));
                cartItemResponseObj = cartItemRepository.save(cartItemToBeSaved);
            } else {
                cartItemRepository.deleteById(cartItemToBeSaved.getId());
                cart.setRestaurant(null);
                cartRepository.save(cart);
                cartItemResponseObj = existingSameFoodItems.get(0);
                cartItemResponseObj.setQuantity(0);
            }
        }

        return Either.right(com.urbaneats.dto.CartItemResponseDto.builder()
                .id(cartItemResponseObj.getId())
                .cartId(cartItemResponseObj.getCart().getId())
                .foodId(cartItemResponseObj.getFood().getId())
                .foodItemName(cartItemResponseObj.getFood().getName())
                .restaurantId(cartItemResponseObj.getRestaurant().getId())
                .restaurantName(cartItemResponseObj.getRestaurant().getName())
                .quantity(cartItemResponseObj.getQuantity())
                .totalPrice(cartItemResponseObj.getTotalPrice())
                .isVeg(cartItemResponseObj.getFood().isVegetarian())
                .build());

    }

    public String clearCart(String userId) {
        Optional<List<Cart>> cartList = cartRepository.findByCustomerId(userId);
        if(cartList.isPresent() && !cartList.get().isEmpty()) {
            Cart cart = cartList.get().get(0);
            cart.setRestaurant(null);
            cart.setTotal(0);

            cartRepository.save(cart);
            Long deleteCount = cartItemRepository.deleteAllByCartId(cart.getId());
            return String.format("%s cart items have been deleted", deleteCount);
        }
        return "No cart exists for the specified user";
    }

    public Either<Error, String> updateQuantity(Long cartItemId, int quantity) {
        return cartItemRepository.findById(cartItemId)
                    .<Either<Error, String>> map(cartItem -> {
                    cartItem.setQuantity(quantity);
                    cartItemRepository.save(cartItem);
                    return Either.right("Successfully updated the quantity of cart item with id: " + cartItemId);
                })
                .orElseGet(() -> Either.left(new Error(ErrorType.NOT_FOUND, "CART_ITEM_NOT_FOUND")));
    }

    public Either<Error, String> removeCartItem(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .<Either<Error, String>> map(cartItem -> {
                    cartItemRepository.delete(cartItem);
                    return Either.right(String.format("Successfully deleted the cart item with id: %s associated with cart id: %s",
                            cartItemId, cartItem.getCart().getId()));
                })
                .orElseGet(() -> Either.left(new Error(ErrorType.NOT_FOUND, "CART_ITEM_NOT_FOUND")));
    }

    public Integer calculateCartTotalAmount(Long cartId) {
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cartId);
        return cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(0, Integer::sum);
    }

    public String clearCart(Long cartId) {
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cartId);
        cartItemRepository.deleteAll(cartItems);

        return "Deleted cart items associated with cart id: " + cartId;
    }

    public Either<Error, String> saveCartData(CartRequestDto cartRequestDto, String userId) {
        Optional<List<Cart>> cartData = cartRepository.findByCustomerId(userId);

        if(cartData.isEmpty())
            return Either.left(new Error(ErrorType.CART_DOES_NOT_EXIST, "Invalid user!! No cart exists for the user"));

        Cart cart = cartData.get().get(0);

        Optional<Restaurant> restaurantEntity = restaurantRepository.findById(cartRequestDto.getRestaurantId());
        if(restaurantEntity.isEmpty())
            return Either.left(new Error(ErrorType.RESTAURANT_NOT_FOUND, "Invalid restaurant id provided"));

        cart.setRestaurant(restaurantEntity.get());
//        cart.setTotal(cartRequestDto.getCartItems().stream().reduce(0, (sum, cartItem) -> sum + (cartItem));
        cartRepository.save(cart);

        List<Food> foodItems = new ArrayList<>();
        for (CartItemSubDtoForSaveCart cartItem : cartRequestDto.getCartItems()) {
            Either<Error, Food> food = foodService.findById(cartItem.getFoodId());
            if(food.isLeft()) return Either.left(new Error(ErrorType.FOOD_ID_INVALID, String.format("Food id - %s is invalid", cartItem.getFoodId())));
            foodItems.add(food.get());
        }

        List<CartItem> cartItemList = foodItems.stream()
                .map(foodItem -> CartItem.builder()
                        .food(foodItem)
                        .cart(cart)
                        .restaurant(restaurantEntity.get())
                        .quantity(cartRequestDto.getCartItems().stream().filter(cartItem -> Objects.equals(cartItem.getFoodId(), foodItem.getId())).toList().get(0).getQuantity())
                        .build()
                )
                .toList();

        cartItemRepository.saveAll(cartItemList);
        return Either.right("Cart data saved successfully");

    }


//    updateCartitemQuantity(cartItemid, quantity)
//    removeitemfromcart(cartItemid)
//    AddItemToCart -
//    cartTotal
//    findCartById
//    findCartByUserId - implemented in user controller - findByUserId
//    clearCart
}
