package com.meteorinc.thegateway.interfaces.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meteorinc.thegateway.application.GatewayEventFacade;
import com.meteorinc.thegateway.domain.event.EventDTO;
import com.meteorinc.thegateway.interfaces.event.dto.EventCreationResponse;
import com.meteorinc.thegateway.interfaces.event.requests.CertifiedUploadRequest;
import com.meteorinc.thegateway.interfaces.event.requests.CheckInRequest;
import com.meteorinc.thegateway.interfaces.event.requests.EventCreationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/gateway/event")
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EventResource {

    GatewayEventFacade gatewayEventFacade;

    @PostMapping
    public ResponseEntity<EventCreationResponse> createEvent(@NonNull @RequestHeader("Authorization") final String token,@RequestBody @NonNull @Valid EventCreationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(gatewayEventFacade.createEvent(request, token));
    }

    @GetMapping("/{eventCode}")
    public ResponseEntity<EventDTO> findEvent(@PathVariable("eventCode") UUID eventCode) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(gatewayEventFacade.findEvent(eventCode));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<EventDTO>> findEvent(@NonNull @RequestHeader("Authorization") final String token)  {
        return ResponseEntity.status(HttpStatus.OK).body(gatewayEventFacade.findEvents(token));
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> findAllEvents(){
        return ResponseEntity.status(HttpStatus.OK).body(gatewayEventFacade.findAllEvents());
    }

    @GetMapping(value = "/generate-qrcode/{eventCode}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQRCode(@PathVariable("eventCode") UUID eventCode){
        return gatewayEventFacade.generateQRCode(eventCode);
    }


    @PostMapping("/check-in/{eventCode}")
    public ResponseEntity<Void> doCheckIn(@NonNull @RequestHeader("Authorization") final String token,
                                              @PathVariable("eventCode") UUID eventCode,
                                              @RequestBody @NonNull final CheckInRequest request){
        gatewayEventFacade.doCheckIn(token, eventCode, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/certificate/upload/{eventCode}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> uploadCertifiedFile(@RequestParam("file") @NonNull final MultipartFile file,
                                                    @PathVariable("eventCode") UUID eventCode,
                                                    @NonNull final HttpServletRequest request) {
        gatewayEventFacade.uploadCert(file, eventCode, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/certificate/{eventCode}")
    public ResponseEntity<byte[]> getCertificate(@PathVariable("eventCode") UUID eventCode) {
        final var cert = gatewayEventFacade.loadCert(eventCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("cert.pdf").build());
        headers.setContentLength(cert.length);

        return new ResponseEntity<>(cert, headers, HttpStatus.OK);
    }

}
