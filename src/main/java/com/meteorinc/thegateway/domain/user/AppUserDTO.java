package com.meteorinc.thegateway.domain.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppUserDTO {
    String name;

    String email;

    String document;

    String documentType;
}
