package com.urbaneats.repository;

import com.urbaneats.model.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends CrudRepository<Cart, Long> {

    public Optional<List<Cart>> findByCustomerId(String customerId);
}
