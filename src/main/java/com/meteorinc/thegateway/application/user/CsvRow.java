package com.meteorinc.thegateway.application.user;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CsvRow {
    String email;

    String name;

    String document;

    String checkInDate;

    String metadata;

}
