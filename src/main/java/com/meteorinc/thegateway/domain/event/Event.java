package com.meteorinc.thegateway.domain.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meteorinc.thegateway.domain.location.Location;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event_name", nullable = false, length = 100)
    String name;

    @Column(name = "event_owner_code", nullable = false, length = 36)
    UUID ownerCode;

    @Column(name = "event_description", nullable = false)
    String description;

    @Column(name = "dat_start")
    LocalDateTime startsAt;

    @Column(name = "dat_finishing")
    LocalDateTime finishAt;

    @Column(name = "dat_creation", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "dat_update", nullable = false)
    LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    Location location;


    public EventDTO toDTO() throws JsonProcessingException {

        return EventDTO.builder()
                .eventName(this.name)
                .eventDescription(this.description)
                .ownerCode(this.ownerCode)
                .startsAt(this.startsAt)
                .location(this.location.toDTO())
                .build();
    }
}