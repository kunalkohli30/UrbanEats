package com.urbaneats.response;

import com.urbaneats.model.USER_ROLE;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String JwtToken;
    private String message;
    private USER_ROLE userRole;
}
