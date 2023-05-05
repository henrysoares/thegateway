package com.meteorinc.thegateway.application.event;

import com.meteorinc.thegateway.application.event.exceptions.EventNotFoundException;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.event.EventDTO;
import com.meteorinc.thegateway.domain.event.EventRepository;
import com.meteorinc.thegateway.domain.location.Location;
import com.meteorinc.thegateway.interfaces.dto.EventCreationResponse;
import com.meteorinc.thegateway.interfaces.requests.EventCreationRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EventService {

    private static final double DEFAULT_DURATION = 2.0;

    private static final long SECONDS_CONSTANT = 3600;

    EventRepository eventRepository;

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
                    .parameters(eventCreationRequest.getLocation().getParameters().toString())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            final Event event = Event.builder()
                    .name(eventCreationRequest.getName())
                    .ownerCode(ownerCode)
                    .eventCode(UUID.randomUUID())
                    .location(location)
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
                    .ownerCode(event.getOwnerCode())
                    .startsAt(event.getStartsAt())
                    .finishesAt(event.getFinishesAt())
                    .location(event.getLocation().toDTO())
                    .build();

        }
        catch (Exception exception){
            throw new RuntimeException();
        }

    }

    public List<EventDTO> findAllEvents(){
        return eventRepository.findAll().stream().map(Event::toDTO).collect(Collectors.toList());
    }

    public EventDTO findEvent(@NonNull final UUID eventCode){
        return eventRepository.findByEventCode(eventCode).orElseThrow(EventNotFoundException::new).toDTO();
    }

    private long convertToSeconds(double duration){
        return (long) (duration * SECONDS_CONSTANT);
    }

}