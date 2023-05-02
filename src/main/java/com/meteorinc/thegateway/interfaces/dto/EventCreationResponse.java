package com.meteorinc.thegateway.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.meteorinc.thegateway.interfaces.requests.LocationCreationRequest;
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

    UUID ownerCode;

    String eventDescription;
    
    LocalDateTime startsAt;

    @NotNull
    @NotBlank
    LocationCreationRequest location;


}