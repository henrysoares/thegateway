package com.meteorinc.thegateway.interfaces.user.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Builder
public class LoginRequest {

    @NotNull
    String email;

    @NotNull
    String password;


}
