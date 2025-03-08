package com.urbaneats.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.urbaneats.config.JwtProvider;
import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.UserDto;
import com.urbaneats.model.USER_ROLE;
import com.urbaneats.model.User;
import com.urbaneats.repository.UserRepository;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserService(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    public Either<Error, User> findUserByJwtToken(String jwtToken) {
        String email = jwtProvider.getEmailFromJwtToken(jwtToken);
        User user = userRepository.findByEmail(email);
        if(Objects.isNull(user))
            return Either.left(new Error(ErrorType.USER_NOT_FOUND, "User not found from the provided jwt token"));
        return Either.right(user);
    }

    public Either<Error, UserDto> findUserByAuthToken(String token) throws FirebaseAuthException {

        FirebaseToken decodedToken = null;

        try {
            //verifies token to firebase server
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (FirebaseAuthException e) {
            return Either.left(new Error(ErrorType.AUTH_TOKEN_INVALID, "Auth token has expired or invalid. Kindly sign in again"));
        }

        String uid= decodedToken.getUid();
        String email = decodedToken.getEmail();

        UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
        return Either.right(UserDto.builder()
                .uid(userRecord.getUid())
                .fullName(userRecord.getDisplayName())
                .email(userRecord.getEmail())
                .user_role(userRecord.getCustomClaims() != null && userRecord.getCustomClaims().get("role") != null ?
                        USER_ROLE.valueOf(userRecord.getCustomClaims().get("role").toString()) : null)
                .imageUrl(userRecord.getPhotoUrl())
                .phoneNumber(userRecord.getPhoneNumber())
                .build());

//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", Arrays.asList("customer"));

//        UserRecord user = FirebaseAuth.getInstance().getUser(uid);
//        UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(uid)
//                .setCustomClaims(claims)
//                .setPhoneNumber("+918368759436");
//        FirebaseAuth.getInstance().updateUser(updateRequest);

//        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);


    }

    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if(Objects.isNull(user))
            throw new Exception("User not found");
        return user;
    }

    public User updateUserRole(USER_ROLE user_role, Long userId) {
        Optional<User> userData = userRepository.findById(userId);
        User updatedUser = User.builder()
                .id(userId)
                .fullName(userData.get().getFullName())
                .email(userData.get().getEmail())
                .password(userData.get().getPassword())
                .user_role(user_role)
                .favourites(userData.get().getFavourites())
//                .customer_orders(userData.get().getCustomer_orders())
//                .addresses(userData.get().getAddresses())
                .build();
        return userRepository.save(updatedUser);
    }
}
