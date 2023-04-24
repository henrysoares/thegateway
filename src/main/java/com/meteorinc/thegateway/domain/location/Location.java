package com.meteorinc.thegateway.domain.location;

import com.fasterxml.jackson.databind.JsonNode;
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

    @Column(name = "LOCATION_TYPE", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    LocationType type;

    @Column(name = "LOCATION_PARAMTERS", nullable = false)
    JsonNode parameters;

    @Column(name = "dat_creation", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "dat_update", nullable = false)
    LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "location")
    Event event;

}
