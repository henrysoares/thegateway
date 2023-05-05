package com.meteorinc.thegateway.domain.event;

import com.meteorinc.thegateway.domain.location.LocationDTO;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class EventDTO implements Serializable {

    @NonNull
    String eventName;

    @NonNull
    UUID ownerCode;

    @NonNull
    String eventDescription;

    @NonNull
    LocalDateTime startsAt;

    LocationDTO location;

}
