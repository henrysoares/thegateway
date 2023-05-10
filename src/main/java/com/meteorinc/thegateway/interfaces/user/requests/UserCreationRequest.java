package com.meteorinc.thegateway.interfaces.user.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Builder
public class UserCreationRequest {

    @NotNull
    String email;

    @NonNull
    String name;

    @NonNull
    String document;

    @NonNull
    String documentType;

    @NotNull
    String password;
}
