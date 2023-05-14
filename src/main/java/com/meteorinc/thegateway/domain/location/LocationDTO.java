package com.meteorinc.thegateway.domain.location;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
public class LocationDTO implements Serializable {

    @NonNull
    LocationType type;

    @NotNull
    @NotBlank
    String latitude;

    @NotNull
    @NotBlank
    String longitude;


    @NonNull
    JsonNode metadata;

}
