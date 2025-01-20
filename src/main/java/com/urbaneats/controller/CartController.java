package com.urbaneats.controller;

import com.urbaneats.dto.cartResponse.CartItemRequestDto;
import com.urbaneats.dto.Error;
import com.urbaneats.dto.ErrorType;
import com.urbaneats.dto.cartResponse.CartRequestDto;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.model.Cart;
import com.urbaneats.model.User;
import com.urbaneats.response.ResponseBody;
import com.urbaneats.service.CartService;
import com.urbaneats.service.UserService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.urbaneats.response.ResponseBody.ResponseBodyBuilder;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getCartData(HttpServletRequest request) {
        String userId = request.getAttribute("uid").toString();
        return new ResponseEntity<>(cartService.getCartData(userId), HttpStatus.OK);
    }

    @PostMapping("/cartItem")
    public ResponseEntity<?> saveCartItem(@RequestBody CartItemRequestDto cartItemRequestDto,
                                          HttpServletRequest request) {

        String userId = request.getAttribute("uid").toString();

        return cartService.addItemToCart(cartItemRequestDto, userId)
                .fold(ErrorResponseHandler::respondError,
                        response -> cartItemRequestDto.getOperation().equalsIgnoreCase("ADD") ?
                                new ResponseEntity<>(response, HttpStatus.OK) :
                                response == null ? new ResponseEntity<>("No cart item exists with the provided foodId", HttpStatus.OK) :
                                        new ResponseEntity<>(response, HttpStatus.OK)
                        // for subtract operation, if response is null then it means no cart item found for the provided foodId
                );
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(HttpServletRequest request) {
        String userId = request.getAttribute("uid").toString();
        return new ResponseEntity<>(cartService.clearCart(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveCartData(@RequestBody CartRequestDto cartRequestDto,
                                          HttpServletRequest request) {

        String userId = request.getAttribute("uid").toString();

        return cartService.saveCartData(cartRequestDto, userId)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findCartById(@PathVariable Long id) {
        Optional<Cart> cartById = cartService.findCartById(id);
        if(cartById.isEmpty())
            return new ResponseEntity<>(new Error(ErrorType.NOT_FOUND, "CART_NOT_FOUND"), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(cartById, HttpStatus.OK);
    }



    @PutMapping("/cartItem/{id}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id,
                                            @RequestParam("updatedQuantity") final Integer updatedQuantity) {
        return cartService.updateQuantity(id, updatedQuantity)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @GetMapping("/{id}/getTotal")
    public ResponseEntity<?> calculateTotalAmount(@PathVariable Long id) {
        return new ResponseEntity<>(cartService.calculateCartTotalAmount(id), HttpStatus.OK);
    }

    @DeleteMapping("/clear/{id}")
    public ResponseEntity<?> clearCart(@PathVariable Long id){
        return Try.of( () -> cartService.clearCart(id))
                .toEither()
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR,
                        String.format("Some uncatched error occurred. Error message: %s, stacktrace: %s",
                            throwable.getMessage(), ExceptionUtils.getStackTrace(throwable))))
                .map(response -> ResponseBody.builder()
                        .success(true)
                        .message(response)
                        .data(null)
                        .build()
                )
                .fold(ErrorResponseHandler::respondError,
                        responseBody -> new ResponseEntity<>(responseBody, HttpStatus.OK));
    }

    @DeleteMapping("/cartItem/{id}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long id) {

        return cartService.removeCartItem(id)
                .map(response -> ResponseBodyBuilder.success(true).message(response).build())
                .fold(ErrorResponseHandler::respondError,
                        responseBody -> new ResponseEntity<>(responseBody, HttpStatus.OK));
    }


}
//    findCartByUserId - implemented in user controller - findByUserId
//    clear cart
//    calculate total
//     removeItemFromCart
//    updateCartItemQuantity
//    findCartById
