package com.meteorinc.thegateway.interfaces.event.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
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
    LocationCreationRequest location;

    @NotNull
    String name;

    @NotBlank
    String description;

    @NotNull
    LocalDateTime startingDate;

    @NotNull
    double durationHours;

}