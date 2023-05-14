package com.meteorinc.thegateway.application.event;

import com.meteorinc.thegateway.application.event.exceptions.EventNotFoundException;
import com.meteorinc.thegateway.application.user.TokenService;
import com.meteorinc.thegateway.domain.checkin.CheckIn;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.event.EventDTO;
import com.meteorinc.thegateway.domain.event.EventRepository;
import com.meteorinc.thegateway.domain.location.Location;
import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.interfaces.event.dto.EventCheckInResponse;
import com.meteorinc.thegateway.interfaces.event.dto.EventCreationResponse;
import com.meteorinc.thegateway.interfaces.event.requests.EventCreationRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EventService {

    private static final double DEFAULT_DURATION = 2.0;

    private static final long SECONDS_CONSTANT = 3600;

    EventRepository eventRepository;

    TokenService tokenService;

    /**
     * Faz a criação do evento a partir do objeto {@link EventCreationRequest}
     *
     * @param eventCreationRequest
     * @return
     */
    public EventCreationResponse createEvent(@NonNull final EventCreationRequest eventCreationRequest, @NonNull final UUID ownerCode){
        try {
            final double duration = eventCreationRequest.getDurationHours() == 0 ?
                    DEFAULT_DURATION : eventCreationRequest.getDurationHours();

            final Location location = Location.builder()
                    .type(eventCreationRequest.getLocation().getType())
                    .metadata(eventCreationRequest.getLocation().getMetadata().toString())
                    .latitude(eventCreationRequest.getLocation().getLatitude())
                    .longitude(eventCreationRequest.getLocation().getLongitude())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            final Event event = Event.builder()
                    .name(eventCreationRequest.getName())
                    .ownerCode(ownerCode)
                    .eventCode(UUID.randomUUID())
                    .location(location)
                    .isNetWorkValidationAvailable(eventCreationRequest.isNetworkValidationEnabled())
                    .description(eventCreationRequest.getDescription())
                    .startsAt(eventCreationRequest.getStartingDate())
                    .finishesAt(eventCreationRequest.getStartingDate()
                            .plusSeconds(convertToSeconds(duration)))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            eventRepository.save(event);

            return EventCreationResponse.builder()
                    .eventName(event.getName())
                    .eventCode(event.getEventCode())
                    .eventDescription(event.getDescription())
                    .isNetworkValidationEnabled(event.isNetWorkValidationAvailable())
                    .ownerCode(event.getOwnerCode())
                    .startsAt(event.getStartsAt())
                    .finishesAt(event.getFinishesAt())
                    .location(event.getLocation().toDTO())
                    .build();

        }
        catch (Exception exception){
            log.error("Was not possible to create the event.", exception);
            throw new RuntimeException();
        }

    }

    public List<EventDTO> findEvents(@NonNull final AppUser user){


        return eventRepository.findByOwnerCode(UUID.fromString(user.getUserCode().toString()))
                .orElse(Collections.emptyList()).stream().map(Event::toDTO).collect(Collectors.toList());
    }

    public List<EventDTO> findAllEvents(){
        return eventRepository.findAll().stream().map(Event::toDTO).collect(Collectors.toList());
    }

    public Event findEvent(@NonNull final UUID eventCode){
        return eventRepository.findByEventCode(eventCode).orElseThrow(EventNotFoundException::new);
    }

    private long convertToSeconds(double duration){
        return (long) (duration * SECONDS_CONSTANT);
    }

}