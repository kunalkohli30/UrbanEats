package com.urbaneats.exception;


import lombok.Getter;

public class AuthorizationFailedException extends RuntimeException{

    @Getter
    private String errorCode;
    private String message;

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuthorizationFailedException() {
        super();
    }

    public AuthorizationFailedException(String errorCode, String message) {
        this.errorCode  = errorCode;
        this.message = message;
    }
}
