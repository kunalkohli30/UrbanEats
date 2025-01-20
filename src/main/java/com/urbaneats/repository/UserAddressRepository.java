package com.urbaneats.repository;

import com.urbaneats.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    public List<UserAddress> findByUserId(String userId);

    public Optional<List<UserAddress>> findByUserIdAndAddressName(String userId, String addressName);
}
