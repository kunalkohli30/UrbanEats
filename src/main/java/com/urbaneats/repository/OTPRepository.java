package com.urbaneats.repository;

import com.urbaneats.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByEmail(String email);
    Optional<OTP> findByUserId(String email);
}
