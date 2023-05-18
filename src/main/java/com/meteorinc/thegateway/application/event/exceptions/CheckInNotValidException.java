package com.meteorinc.thegateway.application.event.exceptions;

import lombok.NonNull;

public class CheckInNotValidException extends RuntimeException{

    public CheckInNotValidException(@NonNull final  String message) {
        super(message);
    }

}
