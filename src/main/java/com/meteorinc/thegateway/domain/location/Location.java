package com.meteorinc.thegateway.domain.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.infrastructure.util.safeoperation.SafeOperations;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "LOCATION")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "location_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    LocationType type;

    @Column(name = "location_latitude", nullable = false)
    String latitude;

    @Column(name = "location_longitude", nullable = false)
    String longitude;

    @Column(name = "location_metadata", nullable = false)
    String metadata;

    @Column(name = "dat_creation", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "dat_update", nullable = false)
    LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "location")
    Event event;

    public LocationDTO toDTO() {
        final ObjectMapper mapper = new ObjectMapper();

        var metadata = SafeOperations.doOperationAndReturn(() -> mapper.readTree(this.metadata))
                .ifNotPossible(null);

        return LocationDTO.builder()
                .type(type)
                .latitude(latitude)
                .longitude(longitude)
                .metadata(metadata)
                .build();
    }

}