package com.meteorinc.thegateway.interfaces.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class LoginResponse implements Serializable {

    String token;

}
