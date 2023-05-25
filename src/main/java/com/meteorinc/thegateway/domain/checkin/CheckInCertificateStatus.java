package com.meteorinc.thegateway.domain.checkin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CheckInCertificateStatus {
    NONE,
    INITIATED,
    PROCESSING,
    DONE,
    SENT,
    ERROR
}
