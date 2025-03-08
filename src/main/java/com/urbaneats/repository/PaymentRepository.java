package com.urbaneats.repository;

import com.urbaneats.model.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payments, Long> {

    public Payments findByOrderId(long orderId);
}
