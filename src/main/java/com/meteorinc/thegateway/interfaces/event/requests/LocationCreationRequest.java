package com.meteorinc.thegateway.interfaces.event.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.meteorinc.thegateway.domain.location.LocationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Builder
public class LocationCreationRequest {

    /** Tipo da locação {@link LocationType} */
    @NotNull
    LocationType type;

    /** Coordenada da latitude do local do evento. */
    @NotNull
    @NotBlank
    String latitude;

    /** Coordenada da longitude do local do evento. */
    @NotNull
    @NotBlank
    String longitude;

    /** Metadata do evento, geralmente onde fica os dados relacionados a network. */
    @NotNull
    @JsonDeserialize(as = ObjectNode.class)
    JsonNode metadata;

}