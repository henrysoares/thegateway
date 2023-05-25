package com.meteorinc.thegateway.application.jobs;

import com.meteorinc.thegateway.application.email.EmailService;
import com.meteorinc.thegateway.application.event.CertificateService;
import com.meteorinc.thegateway.domain.checkin.CheckInCertificateStatus;
import com.meteorinc.thegateway.domain.checkin.CheckInRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CertificateGeneratorJob {

    CheckInRepository checkInRepository;

    CertificateService certificateService;

    EmailService emailService;

    @Scheduled(fixedDelay = 10000)
    public void generateCerts(){
        final var checkIns = checkInRepository.findEligibleUsersForCertificateSending();

        if(checkIns.isPresent() && false){

            if(!checkIns.get().isEmpty()){

                var formattedCheckins = checkIns.get();

                formattedCheckins.forEach(checkIn -> {
                    final var user = checkIn.getAppUser();
                    final var event = checkIn.getEvent();

                    emailService.sendCertificate(user.getEmail(), event.getName(), event.getDescription(), checkIn.getGeneratedCertificate());
                    checkIn.setCertificateStatus(CheckInCertificateStatus.SENT);
                });
                checkInRepository.saveAll(formattedCheckins);
            }

        }
    }

}
