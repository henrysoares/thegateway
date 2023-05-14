package com.meteorinc.thegateway.application;

import com.meteorinc.thegateway.application.event.CheckInService;
import com.meteorinc.thegateway.application.event.EventService;
import com.meteorinc.thegateway.application.qrcode.QRCodeService;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.event.EventDTO;
import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.domain.user.AppUserService;
import com.meteorinc.thegateway.interfaces.event.dto.EventCreationResponse;
import com.meteorinc.thegateway.interfaces.event.requests.CheckInRequest;
import com.meteorinc.thegateway.interfaces.event.requests.EventCreationRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GatewayEventFacade {

    AppUserService appUserService;

    EventService eventService;

    CheckInService checkInService;

    QRCodeService qrCodeService;

    public EventCreationResponse createEvent(@NonNull final EventCreationRequest eventCreationRequest, @NonNull final String creatorToken){
        return eventService.createEvent(eventCreationRequest, appUserService.findUserByToken(creatorToken).getUserCode());
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


    public void doCheckIn(@NonNull final String rawToken, @NonNull final UUID eventCode, @NonNull final CheckInRequest request){

        final AppUser user = appUserService.findUserByToken(rawToken);
        final Event event = eventService.findEvent(eventCode);

        checkInService.doCheckIn(user, event, request);
    }

}
