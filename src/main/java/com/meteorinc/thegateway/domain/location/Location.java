package com.meteorinc.thegateway.domain.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meteorinc.thegateway.domain.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Entity
@Table(name = "LOCATION")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "location_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    LocationType type;

    @Column(name = "location_parameters", nullable = false)
    String parameters;

    @Column(name = "dat_creation", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "dat_update", nullable = false)
    LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "location")
    Event event;

    public LocationDTO toDTO() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();


        return LocationDTO.builder()
                .type(type)
                .parameters(mapper.readTree(parameters))
                .build();
    }

}