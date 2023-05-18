package com.meteorinc.thegateway.application.event.exceptions;

public class CertificateException extends RuntimeException{
    public CertificateException() {
    }

    public CertificateException(String message) {
        super(message);
    }

    public CertificateException(String message, Throwable cause) {
        super(message, cause);
    }
}
