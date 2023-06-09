package com.meteorinc.thegateway.domain.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meteorinc.thegateway.application.event.exceptions.EventException;
import com.meteorinc.thegateway.domain.checkin.CheckIn;
import com.meteorinc.thegateway.domain.location.Location;
import com.meteorinc.thegateway.infrastructure.converter.UUIDEntityConverter;
import com.meteorinc.thegateway.interfaces.event.dto.EventCreationResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
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

    @Column(name = "event_status", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    EventStatus status;

    @Column(name = "event_code", nullable = false, length = 36)
    @Convert(converter = UUIDEntityConverter.class)
    UUID eventCode;

    @Column(name = "event_owner_code", nullable = false, length = 36)
    @Convert(converter = UUIDEntityConverter.class)
    UUID ownerCode;

    @Column(name = "event_description", nullable = false)
    String description;

    @Column(name = "event_network_validation", nullable = false)
    @Setter
    boolean isNetWorkValidationAvailable;

    @Column(name = "dat_start")
    LocalDateTime startsAt;

    @Column(name = "dat_finishing")
    LocalDateTime finishesAt;

    @Column(name = "dat_creation", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "dat_update", nullable = false)
    LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    Location location;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Certificate certificate;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL)
    CheckIn checkIn;

    public EventCreationResponse buildResponse(){
        return EventCreationResponse.builder()
                .eventName(this.getName())
                .status(this.getStatus())
                .eventCode(this.getEventCode())
                .eventDescription(this.getDescription())
                .isNetworkValidationEnabled(this.isNetWorkValidationAvailable())
                .ownerCode(this.getOwnerCode())
                .startsAt(this.getStartsAt())
                .finishesAt(this.getFinishesAt())
                .location(this.getLocation().toDTO())
                .build();
    }

    public EventDTO toDTO() {

        try {
            return EventDTO.builder()
                    .eventName(this.name)
                    .eventDescription(this.description)
                    .status(this.status)
                    .ownerCode(this.ownerCode)
                    .EventCode(this.eventCode)
                    .startsAt(this.startsAt)
                    .finishesAt(this.finishesAt)
                    .location(this.location.toDTO())
                    .createdAt(this.createdAt)
                    .updatedAt(this.updatedAt)
                    .build();
        }catch (Exception exception){
            //TODO: Melhorar exception handling.
            throw new EventException("Não foi possivel realizar a converesão para DTO.");
        }
    }
}