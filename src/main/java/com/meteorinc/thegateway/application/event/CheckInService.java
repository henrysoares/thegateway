package com.meteorinc.thegateway.application.event;

import com.meteorinc.thegateway.domain.checkin.CheckIn;
import com.meteorinc.thegateway.domain.checkin.CheckInRepository;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.user.AppUser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckInService {

    CheckInRepository checkInRepository;

    public void doCheckIn(@NonNull final AppUser user, @NonNull final Event event){
        final var checkIn = CheckIn.builder()
                .appUser(user)
                .event(event)
                .checkInDate(LocalDateTime.now())
                .checkOutDate(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        checkInRepository.save(checkIn);
    }

}
