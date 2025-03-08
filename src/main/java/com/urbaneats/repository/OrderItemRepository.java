package com.urbaneats.repository;


import com.urbaneats.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    public List<OrderItem> findByOrderId(Long orderId);
}
