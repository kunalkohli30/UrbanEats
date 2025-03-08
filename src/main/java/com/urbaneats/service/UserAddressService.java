package com.urbaneats.service;

import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.UserAddressDto;
import com.urbaneats.model.UserAddress;
import com.urbaneats.repository.UserAddressRepository;
import io.vavr.control.Either;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

    public Either<Error, String> saveUserAddress(UserAddressDto userAddressDto, String uid, String email) {
        ModelMapper modelMapper = new ModelMapper();
        UserAddress userAddressEntity = modelMapper.map(userAddressDto, UserAddress.class);

        Optional<List<UserAddress>> addressWithGivenName = userAddressRepository.findByUserIdAndAddressName(uid, userAddressDto.getAddressName());
        if (addressWithGivenName.isPresent() && !addressWithGivenName.get().isEmpty())
            return Either.left(new Error(ErrorType.ADDRESS_NAME_ALREADY_EXISTS,
                    "Address name already exists for the user"));

        userAddressEntity.setUserId(uid);
        userAddressRepository.save(userAddressEntity);
        return Either.right("Address saved successfully");
    }

    public Either<Error, String> deleteUserAddress(Long userAdrId, String userId) {

        return userAddressRepository.findById(userAdrId)
                .<Either<Error, String>>map(userAdr -> {
                    if (userAdr.getUserId().equalsIgnoreCase(userId)) {
                        userAddressRepository.deleteById(userAdrId);
                        return Either.right("USER_ADDRESS_DELETED");
                    }
                    return Either.left(new Error(ErrorType.VALIDATION_FAILED, "Address not linked to the signed in user"));
                })
                .orElse(Either.left(new Error(ErrorType.VALIDATION_FAILED, "Address does not exist")));
    }

    public UserAddress findById(Long addressId) {
        return userAddressRepository.findById(addressId).orElse(null);
    }
}
