package com.meteorinc.thegateway.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meteorinc.thegateway.application.qrcode.QRCodeService;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.application.event.EventService;
import com.meteorinc.thegateway.domain.event.EventDTO;
import com.meteorinc.thegateway.interfaces.dto.EventCreationResponse;
import com.meteorinc.thegateway.interfaces.requests.EventCreationRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/gateway")
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class GatewayResource {

    EventService eventService;

    QRCodeService qrCodeService;

    @PostMapping("/event")
    public ResponseEntity<EventCreationResponse> createEvent(@RequestBody @NonNull @Valid EventCreationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request, UUID.randomUUID()));
    }

    @GetMapping("/{eventCode}")
    public ResponseEntity<EventDTO> findEvent(@PathVariable("eventCode") UUID eventCode) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findEvent(eventCode));
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> findAllEvents(){
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findAllEvents());
    }

    @GetMapping(value = "/generate-qrcode/{eventCode}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQRCode(@PathVariable("eventCode") UUID eventCode){
        return qrCodeService.generateQRCode(eventCode);
    }

}
