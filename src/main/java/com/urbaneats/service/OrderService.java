package com.urbaneats.service;

import com.razorpay.RazorpayException;
import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorAndErrorDetailDto;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.order.*;
import com.urbaneats.enums.OrderStatus;
import com.urbaneats.model.*;
import com.urbaneats.repository.OrderItemRepository;
import com.urbaneats.repository.OrderRepository;
import com.urbaneats.scheduler.OrderStatusUpdateEvent;
import com.urbaneats.scheduler.SchedulerService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FoodService foodService;
    private final UserAddressService userAddressService;
    private final RestaurantService restaurantService;
    private final SchedulerService schedulerService;
    private final RazorpayService razorpayService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, FoodService foodService, UserAddressService userAddressService,
                        RestaurantService restaurantService, SchedulerService schedulerService, RazorpayService razorpayService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.foodService = foodService;
        this.userAddressService = userAddressService;
        this.restaurantService = restaurantService;
        this.schedulerService = schedulerService;
        this.razorpayService = razorpayService;
    }

    private static final int deliveryTimeInSeconds = 25*60;
    private static final int deliveryTimeInMins = 25;

    public Either<ErrorAndErrorDetailDto, OrderResponseDto> placeOrder(String userId, OrderRequestDto orderRequestDto) {

        List<OrderItemRequestDto> orderItems = orderRequestDto.getOrderItems();
        List<Food> foodItems = foodService.findAllById(orderItems.stream().map(OrderItemRequestDto::getFoodId).toList());

        Set<Long> foodIds = foodItems.stream().map(Food::getId).collect(Collectors.toSet());      //to validate if all the food items mentioned in orderItems are present in db

//        VALIDATIONS
        List<OrderItemRequestDto> nonExistentFoodItems = orderItems.stream().filter(item -> !foodIds.contains(item.getFoodId())).toList();

        if (!nonExistentFoodItems.isEmpty())         //return the food items which do not exist in Food table
            return Either.left(ErrorAndErrorDetailDto.builder()
                    .error(new Error(ErrorType.VALIDATION_FAILED, "INVALID_FOOD_ITEMS"))
                    .details(nonExistentFoodItems.stream()
                            .map(item -> Map.entry(item.getFoodId(), item.getFoodItemName()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                    .build()
            );

        if (foodItems.stream().anyMatch(foodItem -> !foodItem.getRestaurant().getId().equals(foodItems.get(0).getRestaurant().getId())))
            return Either.left(ErrorAndErrorDetailDto.builder()
                    .error(new Error(ErrorType.VALIDATION_FAILED, "FOOD_ITEMS_BELONG_TO_DIFFERENT_RESTAURANTS"))
                    .build());

        UserAddress userAddress = userAddressService.findById(orderRequestDto.getAddressId());
        String userAddressString = Objects.nonNull(userAddress) ?
                String.format("%s, %s, %s, %s", userAddress.getFlatNo(), userAddress.getStreet(), userAddress.getCity(),
                        userAddress.getPinCode()) : "";

        Optional<Restaurant> restaurant = restaurantService.findById(orderRequestDto.getRestaurantId());
        if (restaurant.isEmpty())
            return Either.left(ErrorAndErrorDetailDto.builder()
                    .error(new Error(ErrorType.VALIDATION_FAILED, "INVALID_RESTAURANT_ID"))
                    .build());

//       VALIDATIONS END

        Map<Long, Food> foodIdToFoodMap = orderItems.stream()
                .map(orderItem -> Map.entry(orderItem.getFoodId(), foodItems.stream()
                        .filter(foodItem -> foodItem.getId().equals(orderItem.getFoodId()))
                        .findFirst()))
                .filter(entry -> entry.getValue().isPresent())          // filter out just the present values from Optional as all the orderItems are having valid food items. Invalid food id has been handled above.
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));

        // Create Order
        Order order = Order.builder()
                .orderStatus(OrderStatus.PENDING.name())
                .createdAt(orderRequestDto.getCreatedAt())
                .userId(userId)
                .deliveryAddress(userAddressString)
                .addressName(userAddress.getAddressName())
                .restaurant(restaurant.get())
                .totalItem(orderItems.stream().map(OrderItemRequestDto::getQuantity).reduce(0, Integer::sum))
                .totalPrice(orderItems.stream()
                        .map(orderItem -> foodItems.stream()
                                .filter(foodItem -> foodItem.getId().equals(orderItem.getFoodId()))
                                .findFirst()
                                .map(foodItem -> foodItem.getPrice() * orderItem.getQuantity())
                                .orElse(0))
                        .reduce(0, Integer::sum))
                .deliveryFee(orderRequestDto.getDeliveryFee())
                .deliveryTip(orderRequestDto.getDeliveryTip())
                .gstAndFees(orderRequestDto.getGstAndCharges())
                .couponCode(orderRequestDto.getCouponCode())
                .discountAmount(orderRequestDto.getDiscountAmount())
                .deliveryTimeInSeconds(deliveryTimeInSeconds)     // 30 minutes
                .build();
        order.setTotalAmount(order.getTotalPrice() / 100 + order.getDeliveryFee() + order.getDeliveryTip() + order.getGstAndFees() - order.getDiscountAmount());

        Order saved = orderRepository.save(order);
        orderRepository.flush();

        List<OrderItem> orderItemEntities = orderItems.stream()
                .map(orderItem -> OrderItem.builder()
                        .food(foodIdToFoodMap.get(orderItem.getFoodId()))
                        .order(saved)
                        .quantity(orderItem.getQuantity())
                        .totalPrice(foodIdToFoodMap.get(orderItem.getFoodId()).getPrice() * orderItem.getQuantity())
                        .build())
                .toList();

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItemEntities);

//        schedulerService.scheduleTask(saved.getId(), deliveryTimeInMins);

        com.razorpay.Order razorpayPaymentOrder = null;
        try {
            razorpayPaymentOrder = razorpayService.createOrder(order.getTotalAmount());
        } catch (RazorpayException e) {
            log.error("Error creating razorpay payment order for order: {}, userId: {}", order.getId(), userId);
            return Either.left(ErrorAndErrorDetailDto.builder()
                    .error(new Error(ErrorType.FAILED_TO_CREATE_PAYMENT_ORDER, "Unable to create payment order with razorpay"))
                    .build());
        }

        String receiptId = null;
        String razorPayOrderId = null;
        LocalDateTime razorpayOrderCreatedAt = null;

        try {
            razorPayOrderId = razorpayPaymentOrder.toJson().get("id").toString();
            receiptId = razorpayPaymentOrder.toJson().get("receipt").toString();
            Long epochSeconds = Long.valueOf(razorpayPaymentOrder.toJson().get("created_at").toString());
            razorpayOrderCreatedAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
        } catch (Exception e) {
            log.error("Error occurred while fetching razorpay order details for order id: {}", order.getId());
            log.error("payment order: {}", razorpayPaymentOrder.toJson());
            return Either.left(ErrorAndErrorDetailDto.builder()
                    .error(new Error(ErrorType.FAILED_TO_CREATE_PAYMENT_ORDER, "unable to get payment details for order id: " + order.getId()))
                    .build());
        }

//        set razorpay order id in order table
        order.setRazorpayOrderId(razorPayOrderId);
        orderRepository.save(order);

        return Either.right(OrderResponseDto.builder()
                .orderStatus(OrderStatus.PENDING)
                .orderId(order.getId())
                .razorPayOrderId(razorPayOrderId)
                .amount(order.getTotalAmount())
                .receiptId(receiptId)
                .createdAt(orderRequestDto.getCreatedAt())
                .deliveryTime(deliveryTimeInSeconds)
                .orderItems(orderItems)
                .build());
    }

    @EventListener
    public void handleOrderStatusUpdate(OrderStatusUpdateEvent event) {
        log.info("OrderStatusUpdate event triggered");
        updateOrderStatus(event.getOrderId());
    }

    public void updateOrderStatus(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            order.get().setOrderStatus(OrderStatus.CONFIRMED.name());
            orderRepository.save(order.get());
        }
    }

    public List<OrderListResponseDto> getOrders(String userId) {

        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .filter(order -> order.getOrderStatus().equalsIgnoreCase(OrderStatus.CONFIRMED.name()) || order.getOrderStatus().equalsIgnoreCase(OrderStatus.DELIVERED.name()))
                .map(this::mapOrderEntityToResponseDto)
                .toList();
    }

    public OrderListResponseDto getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::mapOrderEntityToResponseDto)
                .orElse(null);
    }

    private OrderListResponseDto mapOrderEntityToResponseDto(Order order) {

        String restaurantImageUrl = restaurantService.getRestaurantImageUrl(order.getRestaurant().getImageId(), order.getRestaurant().getName());
        Payments paymentDetails = razorpayService.findPaymentByOrderId(order.getId());
        return OrderListResponseDto.builder()
                .orderId(order.getId())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .restaurantLocality(order.getRestaurant().getAreaName())
                .restaurantImageUrl(restaurantService.getRestaurantImageUrl(order.getRestaurant().getImageId(), order.getRestaurant().getName()))
                .userId(order.getUserId())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .totalItem(order.getTotalItem())
                .createdAt(order.getCreatedAt())
                .deliveryTimeInSeconds(order.getDeliveryTimeInSeconds())
                .deliveryFee(order.getDeliveryFee())
                .deliveryTip(order.getDeliveryTip())
                .gstAndFees(order.getGstAndFees())
                .discountAmount(order.getDiscountAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .addressName(StringUtils.isNotBlank(order.getAddressName()) ? order.getAddressName() : "Home")
                .paymentMethod(paymentDetails.getPaymentMethod())
                .cardNetwork(paymentDetails.getCardNetwork())
                .cardLast4(paymentDetails.getCardLast4())
                .orderItems(orderItemRepository.findByOrderId(order.getId())
                        .stream()
                        .map(item -> OrderItemResponseDto.builder()
                                .orderItemId(item.getId())
                                .foodId(item.getFood().getId())
                                .foodItemName(item.getFood().getName())
                                .foodItemPrice(item.getFood().getPrice() / 100)
                                .isFoodItemVeg(item.getFood().isVegetarian())
                                .quantity(item.getQuantity())
                                .totalPrice(item.getTotalPrice() / 100)
                                .build()
                        ).toList()
                )
                .build();
    }
}
