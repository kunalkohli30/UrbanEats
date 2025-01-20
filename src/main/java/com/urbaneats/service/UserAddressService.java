package com.urbaneats.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.urbaneats.dto.Error;
import com.urbaneats.dto.ErrorType;
import com.urbaneats.dto.UserAddressDto;
import com.urbaneats.model.UserAddress;
import com.urbaneats.repository.UserAddressRepository;
import io.vavr.control.Either;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class UserAddressService {

    private UserAddressRepository userAddressRepository;

    public UserAddressService(UserAddressRepository userAddressRepository) {
        this.userAddressRepository = userAddressRepository;
    }

    public Either<Error, List<UserAddressDto>> getUserAddress(String uid, String email) {

        ModelMapper modelMapper = new ModelMapper();

        List<UserAddress> userAddresses = userAddressRepository.findByUserId(uid);
        return Either.right(userAddresses.stream()
                .map(userAdr -> modelMapper.map(userAdr, UserAddressDto.class))
                .toList());
    }

    public Either<Error, String> saveUserAddress( UserAddressDto userAddressDto, String uid, String email) {
        ModelMapper modelMapper = new ModelMapper();
        UserAddress userAddressEntity = modelMapper.map(userAddressDto, UserAddress.class);

        Optional<List<UserAddress>> addressWithGivenName = userAddressRepository.findByUserIdAndAddressName(uid, userAddressDto.getAddressName());
        if(addressWithGivenName.isPresent() && !addressWithGivenName.get().isEmpty())
            return Either.left(new Error(ErrorType.ADDRESS_NAME_ALREADY_EXISTS,
                    "Address name already exists for the user"));

        userAddressEntity.setUserId(uid);
        userAddressRepository.save(userAddressEntity);
        return Either.right("Address saved successfully");

    }
}
