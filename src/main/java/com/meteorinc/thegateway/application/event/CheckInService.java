package com.meteorinc.thegateway.application.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meteorinc.thegateway.application.event.exceptions.CheckInNotValidException;
import com.meteorinc.thegateway.domain.checkin.CheckIn;
import com.meteorinc.thegateway.domain.checkin.CheckInCertificateStatus;
import com.meteorinc.thegateway.domain.checkin.CheckInRepository;
import com.meteorinc.thegateway.domain.checkin.CheckInStatus;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.event.EventStatus;
import com.meteorinc.thegateway.domain.location.Location;
import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.interfaces.event.dto.EventCheckInResponse;
import com.meteorinc.thegateway.interfaces.event.requests.EventUserStateValidation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckInService {

    BigDecimal MAXIMUM_RANGE_DIFFERENCE = BigDecimal.valueOf(0.5);

    CheckInRepository checkInRepository;

    public void doCheckIn(@NonNull final AppUser user, @NonNull final Event event, @NonNull final EventUserStateValidation request){
        if(!isOnSameLocation(event, request)){
            throw new CheckInNotValidException("A localização do usuario não bate com a do evento cadastrado.");
        }

        if(event.getStatus().equals(EventStatus.SCHEDULED) || event.getStatus().equals(EventStatus.CANCELLED)){
            throw new CheckInNotValidException("Não é possivel realizar o check-in, o evento ainda não começou ou foi cancelado.");
        }

        if(checkInRepository.findByAppUserAndEvent(user, event).isPresent()){
            throw new CheckInNotValidException("Check-in ja realizado.");
        }

        final var checkIn = CheckIn.builder()
                .appUser(user)
                .event(event)
                .checkInDate(LocalDateTime.now())
                .checkOutDate(null)
                .certificateStatus(CheckInCertificateStatus.NONE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        checkInRepository.save(checkIn);
    }

    public void doCheckOut(@NonNull final AppUser user, @NonNull final Event event){
        if(event.getStatus().equals(EventStatus.SCHEDULED) || event.getStatus().equals(EventStatus.CANCELLED)){
            throw new CheckInNotValidException("Não é possivel realizar o check-out, o evento ainda não começou ou foi cancelado.");
        }

        final var checkIn = checkInRepository.findByAppUserAndEvent(user, event)
                .orElseThrow(() -> new CheckInNotValidException("Check-in não encontrado."));

        checkIn.setUpdatedAt(LocalDateTime.now());
        checkIn.setCheckOutDate(LocalDateTime.now());

        checkInRepository.save(checkIn);
    }


    private boolean isOnSameLocation(@NonNull final Event event, @NonNull final EventUserStateValidation request){
        final boolean isGeolocationValid = validateGeolocation(event.getLocation(),request);

        if(isGeolocationValid && !event.isNetWorkValidationAvailable()) return true;

        return isGeolocationValid && validateNetWork(event.getLocation(), request);

    }

    private boolean validateGeolocation(@NonNull final Location location, @NonNull final EventUserStateValidation request){
        final BigDecimal registeredLatitude = new BigDecimal(location.getLatitude());
        final BigDecimal registeredLongitude = new BigDecimal(location.getLongitude());

        final BigDecimal currentUserLongitude = new BigDecimal(request.getLongitude());
        final BigDecimal currentUserLatitude = new BigDecimal(request.getLatitude());

        final boolean isLatitudeValid = validateAxis(registeredLatitude, currentUserLatitude);
        final boolean isLongitudeValid = validateAxis(registeredLongitude, currentUserLongitude);


        return isLatitudeValid && isLongitudeValid;
    }

    private boolean validateAxis(@NonNull final BigDecimal registeredValue, @NonNull final BigDecimal currentValue){
        return registeredValue.subtract(currentValue).abs().compareTo(MAXIMUM_RANGE_DIFFERENCE) < 0;
    }

    private boolean validateNetWork(@NonNull final Location location, @NonNull final EventUserStateValidation request){
        try{
            ObjectMapper objectMapper = new ObjectMapper();

            final var registeredMetadata = objectMapper.readTree(location.getMetadata());
            final var currentMetadata = request.getMetadata();


            boolean isIpValid = registeredMetadata.get("ip_address").asText().equals(currentMetadata.get("ip_address").asText());
            boolean isSsidValid = registeredMetadata.get("ssid").asText().equals(currentMetadata.get("ssid").asText());

            return isIpValid && isSsidValid;

        } catch (Exception e){
            log.error("Was not possible to validate network fields.",e);
            return true;
        }
    }

}
