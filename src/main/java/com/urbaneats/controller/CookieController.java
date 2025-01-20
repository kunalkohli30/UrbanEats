package com.urbaneats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.urbaneats.controller.CookieService.FirebaseTokenResponse;
import com.urbaneats.dto.Error;
import com.urbaneats.dto.ErrorType;
import com.urbaneats.dto.Tokens;
import io.vavr.control.Either;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@CrossOrigin
@Slf4j
public class CookieController {

    private final CookieService cookieService;

    @Autowired
    public CookieController(CookieService cookieService) {
        this.cookieService = cookieService;
    }


    //    @PostMapping("/cookies")
    public ResponseEntity<?> addCookies(@RequestBody Tokens tokens, HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", tokens.getAuthToken())
                .httpOnly(true)
//                .secure(true) // Set to true if using HTTPS
//                .sameSite("Strict")
                .path("/")
                .maxAge(15 * 60) // 15 minutes
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", tokens.getRefreshToken())
                .httpOnly(true)
//                .secure(true)
//                .sameSite("Strict")
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .build();

        // Add cookies to the response
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok("Login successful. Tokens set as cookies.");
    }

//    Triggered after login, verifies the access token and then adds the access token and refresh tokens as http only cookies
    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestBody Tokens tokens, HttpServletResponse response) {
        if(Objects.isNull(tokens.getAuthToken()) || tokens.getAuthToken().isBlank())
            return ResponseEntity.status(401).body("Access token cookie does not exist, kindly request for a new access token");
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(tokens.getAuthToken());
            String uid = decodedToken.getUid();
            addCookies(tokens, response);
            return ResponseEntity.ok("Token is valid for user:" + uid);
        } catch (FirebaseAuthException e) {
            ObjectMapper mapper = new ObjectMapper();
            Error error = Error.builder()
                    .errorType(e.getAuthErrorCode().toString().equalsIgnoreCase("EXPIRED_ID_TOKEN") ?
                            ErrorType.AUTH_TOKEN_EXPIRED : ErrorType.AUTH_TOKEN_INVALID)
                    .errorMessage(e.getAuthErrorCode().toString().equalsIgnoreCase("EXPIRED_ID_TOKEN") ?
                            "Auth ID token has expired. Get a fresh ID token and try again." :
                            "Failed to parse Auth ID token. Make sure you passed a valid token"
                    )
                    .build();
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<?>  refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        log.info("refresh access token");
        Cookie[] cookies = request.getCookies();
        ResponseEntity<Error> REFRESH_TOKEN_INVALID_ERROR_RESPONSE = ResponseEntity.status(401).body(new Error(ErrorType.REFRESH_TOKEN_INVALID, "Refresh token invalid, kindly login again"));
        if(cookies == null || cookies.length == 0)
            return REFRESH_TOKEN_INVALID_ERROR_RESPONSE;

        List<Cookie> refreshCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equalsIgnoreCase("refresh_token")).toList();

        if(refreshCookie.isEmpty())
            return REFRESH_TOKEN_INVALID_ERROR_RESPONSE;

        return cookieService.refreshAccessToken(refreshCookie.get(0).getValue())
                .peek(firebaseResponse -> {
                    ResponseCookie newAccessTokenCookie = ResponseCookie.from("access_token", firebaseResponse.getAccessToken())
                            .httpOnly(true)
//                            .secure(true)
//                            .sameSite("Strict")
                            .path("/")
                            .maxAge(24 *  60 * 60) // 15 minutes
                            .build();

                    response.addHeader("Set-Cookie", newAccessTokenCookie.toString());
                })
                .fold(error -> REFRESH_TOKEN_INVALID_ERROR_RESPONSE,
                        resp -> ResponseEntity.ok("Access token refreshed"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("access_token", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // Expire immediately

        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // Expire immediately

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("Logged out successfully");
    }
}
