package com.urbaneats.repository;

import com.urbaneats.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByCartId(Long cartId);

    List<CartItem> findByFoodIdAndCartId(Long foodId, Long cartId);

    long deleteAllByCartId(Long cartId);
}
