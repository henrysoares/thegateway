package com.meteorinc.thegateway.infrastructure.security;

public class CredentialsException extends RuntimeException{
    public CredentialsException() {
    }

    public CredentialsException(String message) {
        super(message);
    }

    public CredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
