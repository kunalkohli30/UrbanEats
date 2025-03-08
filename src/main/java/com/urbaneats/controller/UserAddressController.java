package com.urbaneats.controller;


import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.UserAddressDto;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.service.UserAddressService;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/address")
@Slf4j
@CrossOrigin
public class UserAddressController {

    private final UserAddressService userAddressService;

    @Autowired
    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @GetMapping
    public ResponseEntity<?> getUserAddresses(HttpServletRequest request){

        return Try.of(() -> userAddressService.getUserAddress(request.getAttribute("uid").toString(), request.getAttribute("email").toString()))
                .toEither()
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, "OOPS! Some internal error occurred. Sorry for the inconvenience!!!"))
                .flatMap(object -> object)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<?> saveUserAddress(@RequestBody UserAddressDto userAddressDto, HttpServletRequest request){
        return Try.of(() -> userAddressService.saveUserAddress(userAddressDto, request.getAttribute("uid").toString(), request.getAttribute("email").toString()))
                .toEither()
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, "OOPS! Some internal error occurred. Sorry for the inconvenience!!!"))
                .flatMap(object -> object)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserAddress(@PathVariable("id") Long userAdrId, HttpServletRequest request){
        return Try.of(() -> userAddressService.deleteUserAddress(userAdrId, request.getAttribute("uid").toString()))
                .toEither()
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, "OOPS! Some internal error occurred. Sorry for the inconvenience!!!"))
                .flatMap(object -> object)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

}
