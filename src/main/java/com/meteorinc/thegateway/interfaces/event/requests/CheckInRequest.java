package com.meteorinc.thegateway.interfaces.event.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Builder
public class CheckInRequest {

    @NotNull
    @NotBlank
    String latitude;

    @NotNull
    @NotBlank
    String longitude;

    @NotNull
    @JsonDeserialize(as = ObjectNode.class)
    JsonNode metadata;

}
