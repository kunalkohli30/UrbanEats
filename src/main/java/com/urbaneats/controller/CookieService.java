package com.urbaneats.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.urbaneats.dto.Error;
import com.urbaneats.dto.ErrorType;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CookieService {

    @Value("${firebase.api.key}")
    private String firebaseApiKey;

    private final RestTemplate restTemplate;

    @Autowired
    public CookieService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Either<Error, FirebaseTokenResponse> refreshAccessToken(String refreshToken) {
        String url = "https://securetoken.googleapis.com/v1/token?key=" + firebaseApiKey;

        // Prepare the request payload
        Map<String, String> payload = Map.of(
                "grant_type", "refresh_token",
                "refresh_token", refreshToken
        );

        // Create the HTTP request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<FirebaseTokenResponse> responseEntity = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, FirebaseTokenResponse.class);

            return Either.right(responseEntity.getBody());
        } catch (Exception e) {
            return Either.left(new Error(ErrorType.REFRESH_TOKEN_INVALID, "Invalid refresh token"));
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FirebaseTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("id_token")
        private String idToken;

        @JsonProperty("expires_in")
        private String expiresIn;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("project_id")
        private String projectId;
    }
}
