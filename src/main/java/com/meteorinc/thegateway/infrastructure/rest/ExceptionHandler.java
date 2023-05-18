package com.meteorinc.thegateway.infrastructure.rest;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.meteorinc.thegateway.application.event.exceptions.CertificateException;
import com.meteorinc.thegateway.application.event.exceptions.CheckInNotValidException;
import com.meteorinc.thegateway.application.event.exceptions.EventException;
import com.meteorinc.thegateway.application.event.exceptions.EventNotFoundException;
import com.meteorinc.thegateway.application.user.exceptions.UserNotFoundException;
import com.meteorinc.thegateway.infrastructure.security.CredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(CheckInNotValidException.class)
    public ResponseEntity<ErrorResponse> handleCheckInException(CheckInNotValidException e) {

        final var response = ErrorResponse.builder().reason(
                e.getMessage()
        ).details(null).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {

        final var response = ErrorResponse.builder().reason(
                "Não foi possivel encontrar as credenciais informadas."
        ).details(null).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({CertificateException.class})
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(Exception e) {

        final var response = ErrorResponse.builder().reason(
                "Não foi possivel realizar o upload do certificado, tente mais tarde."
        ).details(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler({EventNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleEventNotFoundException(Exception e) {

        final var response = ErrorResponse.builder().reason(
                "Não foi possivel encontrar o evento."
        ).details(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({EventException.class})
    public ResponseEntity<ErrorResponse> handleEventException(Exception e) {

        final var response = ErrorResponse.builder().reason(
                "Não foi possivel gerar o evento."
        ).details(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
