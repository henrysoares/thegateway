package com.meteorinc.thegateway.application.event;

import com.meteorinc.thegateway.application.event.exceptions.EventException;
import com.meteorinc.thegateway.application.event.exceptions.EventNotFoundException;
import com.meteorinc.thegateway.application.user.TokenService;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.event.EventDTO;
import com.meteorinc.thegateway.domain.event.EventRepository;
import com.meteorinc.thegateway.domain.event.EventStatus;
import com.meteorinc.thegateway.domain.location.Location;
import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.interfaces.event.dto.EventCreationResponse;
import com.meteorinc.thegateway.interfaces.event.requests.EventDetailsRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
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
     * Faz a criação do evento a partir do objeto {@link EventDetailsRequest}
     *
     * @param eventDetailsRequest
     * @return
     */
    public EventCreationResponse createEvent(@NonNull final EventDetailsRequest eventDetailsRequest, @NonNull final UUID ownerCode){
        try {
            final double duration = eventDetailsRequest.getDurationHours() == 0 ?
                    DEFAULT_DURATION : eventDetailsRequest.getDurationHours();

            final Location location = Location.builder()
                    .type(eventDetailsRequest.getLocation().getType())
                    .metadata(eventDetailsRequest.getLocation().getMetadata().toString())
                    .latitude(eventDetailsRequest.getLocation().getLatitude())
                    .longitude(eventDetailsRequest.getLocation().getLongitude())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            final Event event = Event.builder()
                    .name(eventDetailsRequest.getName())
                    .ownerCode(ownerCode)
                    .status(EventStatus.SCHEDULED)
                    .eventCode(UUID.randomUUID())
                    .location(location)
                    .isNetWorkValidationAvailable(eventDetailsRequest.isNetworkValidationEnabled())
                    .description(eventDetailsRequest.getDescription())
                    .startsAt(eventDetailsRequest.getStartingDate())
                    .finishesAt(eventDetailsRequest.getStartingDate()
                            .plusSeconds(convertToSeconds(duration)))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            eventRepository.save(event);

            return EventCreationResponse.builder()
                    .eventName(event.getName())
                    .status(event.getStatus())
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
            throw new EventException();
        }

    }

    public List<Event> findEventsByStatusAndStartingDateLessThan(@NonNull final EventStatus status,
                                                                 @NonNull final LocalDateTime dateTime){
        return eventRepository.findByStatusAndStartsAtLessThan(status, dateTime).orElse(Collections.emptyList());
    }

    public List<Event> findEventsByStatusAndFinishingDateLessThan(@NonNull final EventStatus status,
                                                                  @NonNull final LocalDateTime dateTime){
        return eventRepository.findByStatusAndFinishesAtLessThan(status, dateTime).orElse(Collections.emptyList());
    }

    public void save(@NonNull final Event event){
        eventRepository.save(event);
    }

    public void save(@NonNull final Collection<Event> event){
        eventRepository.saveAll(event);
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

    public void updateEvent(@NonNull final UUID eventCode, @NonNull final EventDetailsRequest request){
        final var event = eventRepository.findByEventCode(eventCode).orElseThrow(EventNotFoundException::new);
        final var location = event.getLocation();

        final double duration = request.getDurationHours() == 0 ?
                DEFAULT_DURATION : request.getDurationHours();

        location.setType(request.getLocation().getType());
        location.setLatitude(request.getLocation().getLatitude());
        location.setLongitude(request.getLocation().getLongitude());
        location.setMetadata(request.getLocation().getMetadata().asText());
        location.setUpdatedAt(LocalDateTime.now());

        event.setDescription(request.getDescription());
        event.setName(request.getName());
        event.setLocation(location);
        event.setNetWorkValidationAvailable(request.isNetworkValidationEnabled());
        event.setStartsAt(request.getStartingDate());
        event.setFinishesAt(request.getStartingDate().plusSeconds(convertToSeconds(duration)));
        event.setUpdatedAt(LocalDateTime.now());

        eventRepository.save(event);
    }

    public void updateStatus(@NonNull final UUID eventCode, @NonNull final EventStatus status){
        final var event = eventRepository.findByEventCode(eventCode).orElseThrow(EventNotFoundException::new);
        event.setStatus(status);
        event.setUpdatedAt(LocalDateTime.now());

        eventRepository.save(event);
    }

    public void startEvent(@NonNull final UUID eventCode, @NonNull final EventStatus status){
        final var event = eventRepository.findByEventCode(eventCode).orElseThrow(EventNotFoundException::new);

        final var eventDuration = Duration.between(event.getStartsAt(), event.getFinishesAt()).toHours();

        event.setStatus(status);
        event.setStartsAt(LocalDateTime.now());
        event.setFinishesAt(LocalDateTime.now().plusHours(eventDuration));
        event.setUpdatedAt(LocalDateTime.now());

        eventRepository.save(event);
    }

    public void finishEvent(@NonNull final UUID eventCode){
        final var event = eventRepository.findByEventCode(eventCode).orElseThrow(EventNotFoundException::new);

        event.setFinishesAt(LocalDateTime.now());
        event.setStatus(EventStatus.FORCED_FINISHED);
        event.setUpdatedAt(LocalDateTime.now());

        eventRepository.save(event);
    }

    private long convertToSeconds(double duration){
        return (long) (duration * SECONDS_CONSTANT);
    }

}