package com.meteorinc.thegateway.interfaces.event.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.meteorinc.thegateway.domain.checkin.CheckInStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Builder
public class EventCheckInResponse {

    LocalDateTime nextCheck;

    CheckInStatus status;
}
