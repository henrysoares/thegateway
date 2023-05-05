package com.meteorinc.thegateway.domain.location;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
@Builder
public class LocationDTO implements Serializable {

    @NonNull
    LocationType type;

    @NonNull
    JsonNode parameters;

}
