package com.urbaneats.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import com.urbaneats.dto.UserDto;
import com.urbaneats.handler.ErrorResponseHandler;
import com.urbaneats.model.USER_ROLE;
import com.urbaneats.model.User;
import com.urbaneats.service.UserService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> findUserByJwtToken(@RequestHeader("Authorization") String jwtToken) throws Exception {

        return Try.of(() -> userService.findUserByJwtToken(jwtToken))
                .toEither()
                .mapLeft(throwable -> new Error(ErrorType.INTERNAL_SERVER_ERROR, ""))
                .fold(ErrorResponseHandler::respondError,
                        response -> new ResponseEntity<>(response.get(), HttpStatus.OK));

//        return new ResponseEntity<>(userByJwtToken, HttpStatus.OK);
    }

    @PutMapping("/updateUserRole")
    public ResponseEntity<?> updateUserRole(@RequestHeader("Authorization") String jwtToken,
                                        @RequestParam("userRole") USER_ROLE user_role) {
        Either<Error, User> userByJwtToken = userService.findUserByJwtToken(jwtToken);

        if(userByJwtToken.isLeft())
            return ErrorResponseHandler.respondError(userByJwtToken.getLeft());

        User updatedUser = userService.updateUserRole(user_role, userByJwtToken.get().getId());
        return Objects.nonNull(updatedUser) ? new ResponseEntity<>("User Updated Successfully", HttpStatus.OK) :
                ErrorResponseHandler.respondError(new Error(ErrorType.UPDATE_FAILED, "User Update Failed"));

    }

    @GetMapping("/findByUserId")
    public ResponseEntity<?> findByUserId(@RequestHeader("Authorization") String jwtToken) {
        Either<Error, User> userByJwtToken = userService.findUserByJwtToken(jwtToken);
        if(userByJwtToken.isLeft())
            return ErrorResponseHandler.respondError(userByJwtToken.getLeft());

        return new ResponseEntity<>(
                Map.ofEntries(Map.entry("cartId", userByJwtToken.get().getCart().getId())),
                HttpStatus.OK);

    }

    @GetMapping("/userData")
    public ResponseEntity<?> getUserData(HttpServletRequest request) throws FirebaseAuthException {
        Either<Error, UserDto> userDetails = userService.findUserByAuthToken(request.getAttribute("accessToken").toString());
        if(userDetails.isLeft())
            return ErrorResponseHandler.respondError(userDetails.getLeft());

        return new ResponseEntity<>(userDetails.get(), HttpStatus.OK);
    }

    @GetMapping("/phone")
    public ResponseEntity<?> checkPhoneNumber(HttpServletRequest request) throws FirebaseAuthException {
        String userId = request.getAttribute("uid").toString();
        UserRecord userRecord = FirebaseAuth.getInstance().getUser(userId);

        String phoneNumber = userRecord.getPhoneNumber();
        if(StringUtils.isBlank(phoneNumber))
            return ResponseEntity.ok(Map.ofEntries(Map.entry("phone_number_exists", false)));
        else
            return ResponseEntity.ok(Map.ofEntries(Map.entry("phone_number_exists", true), Map.entry("phone_number", phoneNumber)));
    }


}
