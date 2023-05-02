package com.meteorinc.thegateway.domain.event;

import com.meteorinc.thegateway.domain.location.Location;
import com.meteorinc.thegateway.interfaces.requests.EventCreationRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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
    public Event createEvent(@NonNull final EventCreationRequest eventCreationRequest, @NonNull final UUID ownerCode){
            final double duration = eventCreationRequest.getDurationHours() == 0 ?
                    DEFAULT_DURATION : eventCreationRequest.getDurationHours();

            final Location location = Location.builder()
                    .type(eventCreationRequest.getLocation().getType())
                    .parameters(eventCreationRequest.getLocation().getParameters().asText())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            final Event event = Event.builder()
                    .name(eventCreationRequest.getName())
                    .ownerCode(ownerCode)
                    .location(location)
                    .description(eventCreationRequest.getDescription())
                    .startsAt(eventCreationRequest.getStartingDate())
                    .finishAt(eventCreationRequest.getStartingDate()
                            .plusSeconds(convertToSeconds(duration)))
                    .build();

            eventRepository.save(event);

            return event;
    }

    private long convertToSeconds(double duration){
        return (long) (duration * SECONDS_CONSTANT);
    }

}