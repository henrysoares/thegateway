package com.meteorinc.thegateway.interfaces.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meteorinc.thegateway.application.GatewayEventFacade;
import com.meteorinc.thegateway.domain.event.EventDTO;
import com.meteorinc.thegateway.domain.event.EventStatus;
import com.meteorinc.thegateway.interfaces.event.dto.EventCheckInResponse;
import com.meteorinc.thegateway.interfaces.event.dto.EventCreationResponse;
import com.meteorinc.thegateway.interfaces.event.requests.EventUserStateValidation;
import com.meteorinc.thegateway.interfaces.event.requests.EventDetailsRequest;
import com.meteorinc.thegateway.interfaces.event.requests.FireEmailsRequest;
import com.opencsv.CSVWriter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/gateway/event")
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EventResource {

    GatewayEventFacade gatewayEventFacade;

    @PostMapping
    public ResponseEntity<EventCreationResponse> createEvent(
            @NonNull @RequestHeader("Authorization") final String token,
            @RequestBody @NonNull @Valid EventDetailsRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(gatewayEventFacade.createEvent(request, token));
    }

    @GetMapping("/{eventCode}")
    public ResponseEntity<EventDTO> findEvent(@PathVariable("eventCode") UUID eventCode)
            throws JsonProcessingException {
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
    public ResponseEntity<EventCheckInResponse> doCheckIn (
            @NonNull @RequestHeader("Authorization") final String token,
            @PathVariable("eventCode") UUID eventCode,
            @RequestBody @NonNull final EventUserStateValidation request){
        return ResponseEntity.ok(gatewayEventFacade.doCheckIn(token, eventCode, request));
    }

    @PostMapping(value = "/certificate/upload/{eventCode}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
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

    @PostMapping("/certificate/{eventCode}")
    public ResponseEntity<Void> fireEmails(@PathVariable("eventCode") UUID eventCode,
                                           @NonNull @RequestBody final FireEmailsRequest request) {

        return ResponseEntity.ok().build();
    }

    @GetMapping("/generate-statistics/{eventCode}")
    public void generateStatistics(@PathVariable("eventCode") UUID eventCode,
                                   HttpServletResponse response) throws IOException {

        // Define o tipo de conteúdo da resposta como "text/csv"
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"data.csv\"");

        // Cria um escritor CSV usando o PrintWriter
        PrintWriter writer = response.getWriter();
        CSVWriter csvWriter = new CSVWriter(writer);

        // Escreve os dados CSV
        String[] header = {"Nome", "Idade"};
        String[] data1 = {"João", "30"};
        String[] data2 = {"Maria", "25"};
        csvWriter.writeNext(header);
        csvWriter.writeNext(data1);
        csvWriter.writeNext(data2);

        // Fecha o escritor CSV
        csvWriter.close();
    }



    @PatchMapping(value = "/{eventCode}")
    public ResponseEntity<Void> updateEventDetails(@PathVariable("eventCode") UUID eventCode,
                                                   @NonNull @RequestBody final EventDetailsRequest request) {
        gatewayEventFacade.updateEvent(eventCode, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{eventCode}/cancel")
    public ResponseEntity<Void> cancelEvent(@PathVariable("eventCode") UUID eventCode) {
        gatewayEventFacade.updateStatus(eventCode, EventStatus.CANCELLED);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{eventCode}/start")
    public ResponseEntity<Void> startEvent(@PathVariable("eventCode") UUID eventCode) {
        gatewayEventFacade.updateStatus(eventCode, EventStatus.IN_PROGRESS);
        return ResponseEntity.ok().build();
    }

}

