package com.urbaneats.controller;

import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.order.OrderRequestDto;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.service.OrderService;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDto orderRequestDto, HttpServletRequest request) {

        String userId = request.getAttribute("uid").toString();
        return orderService.placeOrder(userId, orderRequestDto)
                .peekLeft(error -> log.error("error occurred while placing order: {}", orderRequestDto.toString()))
                .fold(error -> ErrorResponseHandler.respondError(error.getError()),
                        response -> new ResponseEntity<>(response, HttpStatus.CREATED));
    }


    @GetMapping
    public ResponseEntity<?> getOrders(HttpServletRequest request) {

        String userId = request.getAttribute("uid").toString();
        return Try.of(() -> orderService.getOrders(userId))
                .toEither()
                .peekLeft(throwable -> log.error("Error occurred while getting orders for userId: {}, error: {}, stacktrace: {}",
                        userId, throwable.getMessage(), throwable.getStackTrace()))
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, throwable.getMessage()))
                .fold(ErrorResponseHandler::respondError, ResponseEntity::ok);

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable long orderId, HttpServletRequest request) {

        String userId = request.getAttribute("uid").toString();
        return Try.of(() -> orderService.getOrderDetails(orderId))
                .toEither()
                .peekLeft(throwable -> log.error("Error occurred while getting orders for userId: {}, error: {}, stacktrace: {}",
                        userId, throwable.getMessage(), throwable.getStackTrace()))
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, throwable.getMessage()))
                .fold(ErrorResponseHandler::respondError, ResponseEntity::ok);

    }
}
