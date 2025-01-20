package com.urbaneats.service;

import com.urbaneats.dto.AddressDto;
import com.urbaneats.model.Address;
import com.urbaneats.repository.AddressRepository;
import io.vavr.control.Either;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<AddressDto> getUserAddresses(String userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(address -> AddressDto.builder()
                        .userId(address.getUserId())
                        .id(address.getId())
                        .streetAddress(address.getStreetAddress())
                        .areaName(address.getAreaName())
                        .zipCode(address.getZipCode())
                        .city(address.getCity())
                        .addressName(address.getAddressName())
                        .country(address.getCountry())
                        .build()
                )
                .toList();
    }

    public List<AddressDto> addAddress( AddressDto addressDto, String userId) {
        Address addressEntity = modelMapper.map(addressDto, Address.class);
        Address saved = addressRepository.save(addressEntity);
        return getUserAddresses(userId);
    }

    public String removeAddress( Long addressId, String userId) {
        addressRepository.deleteById(addressId);
        return "ADDRESS_DELETED";
    }
}
