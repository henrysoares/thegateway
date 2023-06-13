package com.meteorinc.thegateway.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meteorinc.thegateway.application.email.EmailService;
import com.meteorinc.thegateway.application.event.CertificateService;
import com.meteorinc.thegateway.application.event.CheckInService;
import com.meteorinc.thegateway.application.event.EventService;
import com.meteorinc.thegateway.application.event.exceptions.CertificateException;
import com.meteorinc.thegateway.application.qrcode.QRCodeService;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.event.EventDTO;
import com.meteorinc.thegateway.domain.event.EventStatus;
import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.application.user.AppUserService;
import com.meteorinc.thegateway.interfaces.event.dto.EventCheckInResponse;
import com.meteorinc.thegateway.interfaces.event.dto.EventCreationResponse;
import com.meteorinc.thegateway.interfaces.event.requests.CertifiedUploadRequest;
import com.meteorinc.thegateway.interfaces.event.requests.EventUserStateValidation;
import com.meteorinc.thegateway.interfaces.event.requests.EventDetailsRequest;
import com.opencsv.CSVWriter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GatewayEventFacade {

    AppUserService appUserService;

    EventService eventService;

    CheckInService checkInService;

    QRCodeService qrCodeService;

    EmailService emailService;

    CertificateService certificateService;

    /**
     * Metodo responsavel por fazer a criação do evento.
     * @param eventDetailsRequest Request de criação do evento.
     * @param creatorToken Token do criador.
     */
    public EventCreationResponse createEvent(@NonNull final EventDetailsRequest eventDetailsRequest,
                                             @NonNull final String creatorToken){

        return eventService.createEvent(
                eventDetailsRequest,
                appUserService.findUserByToken(creatorToken).getUserCode());
    }

    public List<EventDTO> findEvents(@NonNull final String token){
        return eventService.findEvents(appUserService.findUserByToken(token));
    }

    public List<EventDTO> findAllEvents(){
        return eventService.findAllEvents();
    }

    public EventDTO findEvent(@NonNull final UUID eventCode){
        return eventService.findEvent(eventCode).toDTO();
    }

    public byte[] generateQRCode(@NonNull final UUID eventCode){
        return qrCodeService.generateQRCode(eventCode);
    }

    public void sendQRCodeToEmail(@NonNull final UUID eventCode){
        var event = eventService.findEvent(eventCode);
        var owner = appUserService.findUser(event.getOwnerCode());

        emailService.sendQRCode(owner.getEmail(), event.getName(), generateQRCode(eventCode));

    }

    @SneakyThrows
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendStatisticsToEmail(@NonNull final UUID eventCode){
        var event = eventService.findEvent(eventCode);
        var owner = appUserService.findUser(event.getOwnerCode());

        int numberOfCheckIns = checkInService.countCheckIns(event);
        //todo: melhor o codigo e utilizar uma lib para manipular o csv.
        String header = "Email, Nome, Documento, Horario de checkIn, Horario de checkOut, Metadata do usuario. \n";

        StringBuilder csvContent = new StringBuilder();
        csvContent.append(header);
        for (String[] line : gerarCsv(event)) {
            for (String valor : line) {
                csvContent.append(valor).append(",");
            }
            csvContent.append("\n");
        }

        emailService.sendStatistics(owner.getEmail(), event.getName(),csvContent.toString() , numberOfCheckIns, event);

    }

    public List<String[]> gerarCsv(@NonNull final Event event) {
        return checkInService.findCheckInsByEvent(event).stream().map(checkIn -> {
            var user = checkIn.getAppUser();
            return new String[]{user.getEmail(), user.getName(), user.getDocument(), checkIn.getCheckInDate().toString(),
                    checkIn.getCheckOutDate() == null ? "Sem CheckOut" : checkIn.getCheckOutDate().toString()};
        }).collect(Collectors.toList());
    }


    /**
     * Metodo responsavel por realizar o check-in do evento.
     * @param rawToken Token do solicitante.
     * @param eventCode Codigo do evento (lido pelo QRCODE).
     * @param request Requisição para fazer o reconhecimento das coordenadas e da network.
     */
    public void doCheckIn(@NonNull final String rawToken, @NonNull final UUID eventCode,
                          @NonNull final EventUserStateValidation request){

        final AppUser user = appUserService.findUserByToken(rawToken);
        final Event event = eventService.findEvent(eventCode);

        checkInService.doCheckIn(user, event, request);
    }

    /**
     * Metodo responsavel por realizar o check-out do evento.
     * @param rawToken Token do solicitante.
     * @param eventCode Codigo do evento (lido pelo QRCODE).
     */
    public void doCheckOut(@NonNull final String rawToken, @NonNull final UUID eventCode){

        final AppUser user = appUserService.findUserByToken(rawToken);
        final Event event = eventService.findEvent(eventCode);

        checkInService.doCheckOut(user, event);
    }

    /**
     * Metodo que faz o upload de um certificado do evento.
     *
     * @param eventCode Codigo do evento.
     * @param file certificado.
     * @param request request para criação.
     */
    public void uploadCert(@NonNull final MultipartFile file,
                           @NonNull final UUID eventCode,
                           @NonNull final HttpServletRequest request){

        try{
            final var event = eventService.findEvent(eventCode);

            final var objMapper = new ObjectMapper();

            final var certRequest = CertifiedUploadRequest.builder()
                    .name(getParameter("name",request))
                    .description(getParameter("description",request))
                    .metadata(objMapper.readTree(getParameter("metadata", request)))
                    .build();

            certificateService.saveCertificate(file, event,certRequest);
        }catch (Exception exception){
            log.error("Was not possible to upload the certificate.",exception);
            throw new CertificateException("Não foi possivel fazer o updaload do certificado.", exception);
        }
    }

    /**
     * Metodo que faz o carregamento de um certificado de um evento.
     * @param eventCode Codigo do evento.
     *
     * @return Os bytes do certificado.
     */
    public byte[] loadCert(@NonNull final UUID eventCode){
        try {
            final var event = eventService.findEvent(eventCode);

            return certificateService.loadPdf(event);
        }catch (Exception exception){
            log.error("Was not possible to load the PDF.",exception);
            throw new CertificateException("Não foi possivel carregar o arquivo, tente mais tarde.", exception);
        }

    }

    public void updateEvent(@NonNull final UUID eventCode, @NonNull final EventDetailsRequest request){
        eventService.updateEvent(eventCode, request);
    }

    public void updateStatus(@NonNull final UUID eventCode, @NonNull final EventStatus status){
        eventService.updateStatus(eventCode, status);
    }

    public void startEvent(@NonNull final UUID eventCode, @NonNull final EventStatus status){
        eventService.startEvent(eventCode, status);
    }

    public void finishEvent(@NonNull final UUID eventCode){
        eventService.finishEvent(eventCode);
    }

    private String getParameter(@NonNull final String parameterName, @NonNull final HttpServletRequest request){
        return Optional.ofNullable(request.getParameter(parameterName)).orElseThrow(RuntimeException::new);
    }

}
