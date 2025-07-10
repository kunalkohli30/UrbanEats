package com.urbaneats.service;

import com.google.firebase.ErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.urbaneats.config.JwtProvider;
import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.UserDto;
import com.urbaneats.model.Cart;
import com.urbaneats.model.USER_ROLE;
import com.urbaneats.model.User;
import com.urbaneats.repository.CartRepository;
import com.urbaneats.repository.UserRepository;
import com.urbaneats.request.LoginRequest;
import com.urbaneats.response.AuthResponse;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Boolean.FALSE;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    CustomerUserDetailsService customerUserDetailsService;
    @Autowired
    CartRepository cartRepository;


//    Signup method for in house jwt implementation
    public Either<Error, AuthResponse> createUserHandler(User user) {

        Either<Error, Object> savedUser = Optional.ofNullable(userRepository.findByEmail(user.getEmail()))
                .map(existingUserWithSameEmail -> {
                    return Either.left(new Error(ErrorType.EMAIL_ALREADY_REGISTERED, "The provided email is already in use"));
                })
                .orElseGet(() -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return Either.right(userRepository.save(user));
                });

        Cart cart = new Cart();
//        cart.setCustomer((User) savedUser.get());
        cartRepository.save(cart);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtProvider.generateToken(authentication);

        return Either.right(
                AuthResponse.builder()
                        .JwtToken(jwtToken)
                        .message("User registered successfully")
                        .userRole(((User) savedUser.get()).getUser_role())
                        .build()
        );

    }

//    signin method for in house jwt validation
    public AuthResponse signIn(LoginRequest req) {

        Authentication authentication = authenticate(req.getEmail(), req.getPassword());
        String jwtAuthToken = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = AuthResponse.builder()
                .JwtToken(jwtAuthToken)
                .message("User registered successfully")
                .userRole(USER_ROLE.valueOf(authentication.getAuthorities().iterator().next().getAuthority()))
                .build();
        return authResponse;
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid Username");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }

//    signup
    public Either<Error, Map<String, String>> registerUserDetails(UserDto userDto) throws FirebaseAuthException {

        UserRecord userRecord = null;
        try {
             userRecord = FirebaseAuth.getInstance().getUserByEmail(userDto.getEmail());
        } catch (FirebaseAuthException e) {
            if(!e.getErrorCode().equals(ErrorCode.NOT_FOUND)) {
               return Either.left(new Error(ErrorType.FIREBASE_EXCEPTION, e.getMessage()));
            }
        }
        if(Objects.nonNull(userRecord))
            return Either.left(new Error(ErrorType.EMAIL_ALREADY_REGISTERED, "Email already registered with some other user account"));

        CreateRequest createRequest = new CreateRequest();
        createRequest.setEmail(userDto.getEmail());
        createRequest.setPassword(userDto.getPassword());
        createRequest.setDisplayName(userDto.getFullName());
//        createRequest.setCustomClaims(Map.of("role", USER_ROLE.ROLE_CUSTOMER.toString()));
//        createRequest.setPhoneNumber(userDto.getPhoneNumber());
//        createRequest.setPhotoUrl(userDto.getImageUrl());

        // create a new user in firebase auth
        UserRecord user = FirebaseAuth.getInstance().createUser(createRequest);
        // update the recently created user and add custom claims, adding in previous step was not possible
        registerClaimsForNewUser(user.getUid(), Map.ofEntries(Map.entry("role", USER_ROLE.ROLE_CUSTOMER.toString())));

        // return authentication token
        String authToken = FirebaseAuth.getInstance().createCustomToken(user.getUid());
        return Either.right(Collections.singletonMap("authToken", authToken));
    }

    public void registerClaimsForNewUser(String uid, Map<String, Object> claims) throws FirebaseAuthException {

        UpdateRequest updateRequest= new UpdateRequest(uid);
        updateRequest.setCustomClaims(claims);
        FirebaseAuth.getInstance().updateUser(updateRequest);
    }

    public Map<String, Boolean> validateEmail(String email) {

        Boolean isEmailRegistered = true;
        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
        } catch (FirebaseAuthException e) {
            isEmailRegistered = FALSE;
        }

        return Collections.singletonMap("isEmailRegistered", isEmailRegistered);
    }

//    //    Login implemented on fronted
//    public Either<Error, UserDto> loginV2(LoginRequest loginRequest) {
//
//        UserRecord userRecord = null;
//        try {
//            userRecord = FirebaseAuth.getInstance().getUserByEmail(loginRequest.getEmail());
//        } catch (FirebaseAuthException e) {
//            if(e.getErrorCode().equals(ErrorCode.NOT_FOUND)) {
//                return Either.left(new Error(ErrorType.USER_NOT_FOUND, "No user found with provided email address"));
//            } else {
//                return Either.left(new Error(ErrorType.FIREBASE_EXCEPTION, e.getMessage()));
//            }
//        }
//        userRecord.get
//    }
}
