package com.meteorinc.thegateway.infrastructure.converter;

import javax.persistence.AttributeConverter;
import java.util.UUID;

public class UUIDEntityConverter implements AttributeConverter<UUID, String> {
    @Override
    public String convertToDatabaseColumn(UUID attribute) {
        return attribute.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String dbData) {
        return UUID.fromString(dbData);
    }
}
