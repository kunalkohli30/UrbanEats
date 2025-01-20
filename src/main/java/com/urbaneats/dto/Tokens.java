package com.urbaneats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Tokens {

    private String authToken;
    private String refreshToken;
}
