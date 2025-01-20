package com.urbaneats.controller;


import com.urbaneats.commons.Constants;
import com.urbaneats.dto.AddressDto;
import com.urbaneats.dto.ErrorType;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.service.AddressService;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
@Slf4j
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<?> getUserAddresses(HttpServletRequest request) {
        return Try.of(() -> addressService.getUserAddresses(request.getAttribute("uid").toString()))
                .toEither()
                .peekLeft(throwable -> log.error("Exception occurred while getting user addresses. Ex msg: {}, stacktrace: {}",
                        throwable.getMessage(), ExceptionUtils.getStackTrace(throwable)))
                .mapLeft(throwable -> Constants.internalServerError)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<?> addUserAddresses(@RequestBody AddressDto addressDto, HttpServletRequest request) {
        return Try.of(() -> addressService.addAddress(addressDto, request.getAttribute("uid").toString()))
                .toEither()
                .peekLeft(throwable -> log.error("Exception occurred while adding user address. Ex msg: {}, stacktrace: {}",
                        throwable.getMessage(), ExceptionUtils.getStackTrace(throwable)))
                .mapLeft(throwable -> Constants.internalServerError)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @DeleteMapping
    public ResponseEntity<?> removeUserAddresses(@RequestBody Long addressId, HttpServletRequest request) {
        return Try.of(() -> addressService.removeAddress(addressId, request.getAttribute("uid").toString()))
                .toEither()
                .peekLeft(throwable -> log.error("Exception occurred while adding user address. Ex msg: {}, stacktrace: {}",
                        throwable.getMessage(), ExceptionUtils.getStackTrace(throwable)))
                .mapLeft(throwable -> Constants.internalServerError)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }
}
