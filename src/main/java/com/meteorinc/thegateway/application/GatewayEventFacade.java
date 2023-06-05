package com.meteorinc.thegateway.application;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GatewayEventFacade {

    AppUserService appUserService;

    EventService eventService;

    CheckInService checkInService;

    QRCodeService qrCodeService;

    CertificateService certificateService;

    public EventCreationResponse createEvent(@NonNull final EventDetailsRequest eventDetailsRequest, @NonNull final String creatorToken){
        return eventService.createEvent(eventDetailsRequest, appUserService.findUserByToken(creatorToken).getUserCode());
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


    public void doCheckIn(@NonNull final String rawToken, @NonNull final UUID eventCode, @NonNull final EventUserStateValidation request){

        final AppUser user = appUserService.findUserByToken(rawToken);
        final Event event = eventService.findEvent(eventCode);

        checkInService.doCheckIn(user, event, request);
    }

    public void doCheckOut(@NonNull final String rawToken, @NonNull final UUID eventCode){

        final AppUser user = appUserService.findUserByToken(rawToken);
        final Event event = eventService.findEvent(eventCode);

        checkInService.doCheckOut(user, event);
    }


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
