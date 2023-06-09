package com.meteorinc.thegateway.interfaces.event.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.meteorinc.thegateway.domain.event.EventStatus;
import com.meteorinc.thegateway.domain.location.LocationDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Builder
public class EventCreationResponse implements Serializable {

    String eventName;

    EventStatus status;

    UUID ownerCode;

    UUID eventCode;

    String eventDescription;

    boolean isNetworkValidationEnabled;
    
    LocalDateTime startsAt;

    LocalDateTime finishesAt;

    @NotNull
    @NotBlank
    LocationDTO location;


}