package com.meteorinc.thegateway.interfaces.event.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Builder
public class FireEmailsRequest {

    double participationPercentage;

    String dummyEmail;

    String dummySubject;
}
