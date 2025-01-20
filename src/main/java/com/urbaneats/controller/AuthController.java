package com.urbaneats.controller;

import com.urbaneats.dto.Error;
import com.urbaneats.dto.ErrorType;
import com.urbaneats.dto.UserDto;
import com.urbaneats.request.LoginRequest;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.service.AuthService;
import com.urbaneats.model.User;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //    Handler to create new user account
    @PostMapping("/signup")
    public ResponseEntity<?> createUserHandler(@RequestBody User user){

        return Try.of( () -> authService.createUserHandler(user))
                .toEither()
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, "OOPS! Some internal error occurred. Sorry for the inconvenience!!!"))
                .flatMap(object -> object)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest req) {

        return Try.of( () -> authService.signIn(req))
                .toEither()
                .peekLeft(throwable -> log.error("Exception occurred during sign in attempt by user: {}. Exception: {}, error message: {}",
                        req.getEmail(), throwable.getCause(), throwable.getMessage()))
                .mapLeft(throwable -> {
                    if(throwable instanceof UsernameNotFoundException)
                        return new Error(ErrorType.USER_NOT_FOUND, throwable.getMessage());
                    else if(throwable instanceof BadCredentialsException)
                        return new Error(ErrorType.BAD_AUTHENTICATION_CREDENTIALS, throwable.getMessage());
                    else
                        return new Error(ErrorType.INTERNAL_SERVER_ERROR, throwable.getMessage());
                })
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));

    }

    @PostMapping("/signup/v2")
    public ResponseEntity<?> signupV2(HttpServletRequest httpServletRequest,
                                      @RequestBody UserDto userDto) {
        return Try.of( () -> authService.registerUserDetails(userDto))
                .toEither()
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, "OOPS! Some internal error occurred. Sorry for the inconvenience!!!"))
                .flatMap(object -> object)
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }


    @GetMapping("/validateEmail")
    public ResponseEntity<?> validateEmail(@RequestParam("email") String email) {
        return Try.of( () -> authService.validateEmail(email))
                .toEither()
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, "OOPS! Some internal error occurred. Sorry for the inconvenience!!!"))
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response, HttpStatus.OK));
    }

//    Login implemented on fronted
//    @PostMapping("/signin/v2")
//    public ResponseEntity<?> signInV2(@RequestBody LoginRequest req) {
//
//        return Try.of( () -> authService.signIn(req))
//                .toEither()
//                .peekLeft(throwable -> log.error("Exception occurred during sign in attempt by user: {}. Exception: {}, error message: {}",
//                        req.getEmail(), throwable.getCause(), throwable.getMessage()))
//                .mapLeft(throwable -> {
//                    if(throwable instanceof UsernameNotFoundException)
//                        return new Error(ErrorType.USER_NOT_FOUND, throwable.getMessage());
//                    else if(throwable instanceof BadCredentialsException)
//                        return new Error(ErrorType.BAD_AUTHENTICATION_CREDENTIALS, throwable.getMessage());
//                    else
//                        return new Error(ErrorType.INTERNAL_SERVER_ERROR, throwable.getMessage());
//                })
//                .fold(ErrorResponseHandler::respondError,
//                        response -> new ResponseEntity<>(response, HttpStatus.OK));
//
//    }

}
