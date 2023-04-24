package com.meteorinc.thegateway.interfaces;

import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.event.EventService;
import com.meteorinc.thegateway.interfaces.dto.EventCreationResponse;
import com.meteorinc.thegateway.interfaces.requests.EventCreationRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping("/api/gateway")
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class GatewayResource {

    EventService eventService;

    @PostMapping("/event")
    public ResponseEntity<EventCreationResponse> createEvent(@RequestBody @NonNull @Valid EventCreationRequest request){
        final Event event = eventService.createEvent(request, UUID.randomUUID());

        final EventCreationResponse response = EventCreationResponse.builder()
                .eventName(event.getName())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
