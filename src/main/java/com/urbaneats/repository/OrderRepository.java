package com.urbaneats.repository;

import com.urbaneats.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(String userId);
    Optional<Order> findByRazorpayOrderId(String razorPayOrderId);
}
