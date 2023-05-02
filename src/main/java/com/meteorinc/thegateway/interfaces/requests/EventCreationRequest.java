package com.meteorinc.thegateway.interfaces.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Builder
public class EventCreationRequest implements Serializable {

    @NotNull
    @NotBlank
    LocationCreationRequest location;

    @NotNull
    @NotBlank
    String name;

    @NotNull
    @NotBlank
    String description;

    @NotNull
    @NotBlank
    LocalDateTime startingDate;

    @NotNull
    @NotBlank
    double durationHours;

    @NotNull
    @NotBlank
    LocalDateTime creationDate;

}