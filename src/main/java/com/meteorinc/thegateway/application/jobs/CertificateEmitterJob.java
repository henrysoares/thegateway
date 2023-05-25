package com.meteorinc.thegateway.application.jobs;

import com.meteorinc.thegateway.application.email.EmailService;
import com.meteorinc.thegateway.application.event.CertificateService;
import com.meteorinc.thegateway.domain.checkin.CheckInCertificateStatus;
import com.meteorinc.thegateway.domain.checkin.CheckInRepository;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.user.AppUser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CertificateEmitterJob {

    CheckInRepository checkInRepository;
    
    CertificateService certificateService;

    EmailService emailService;

   // @Scheduled(fixedDelay = 20000)
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    @SneakyThrows
    public void processCerts(){
        final var eligibleCheckIns = checkInRepository.findEligibleUsersForCertificateGeneration();

        if(eligibleCheckIns.isPresent()){
            if(!eligibleCheckIns.get().isEmpty()){
                final var checkIns = eligibleCheckIns.get();
                checkIns.forEach(checkIn -> checkIn.setCertificateStatus(CheckInCertificateStatus.PROCESSING));

                checkInRepository.saveAll(checkIns);

                checkIns.forEach( checkIn -> {
                    try {
                        final var cert = generateCert(checkIn.getEvent(), checkIn.getAppUser());

                        checkIn.setGeneratedCertificate(cert);
                        checkIn.setCertificateStatus(CheckInCertificateStatus.DONE);
                    } catch (Exception e) {
                        log.error("Erro ao gerar o PDF.",e);
                        checkIn.setCertificateStatus(CheckInCertificateStatus.ERROR);
                    }

                });

                checkInRepository.saveAll(checkIns);
            }
        }
    }


    @SneakyThrows
    private byte[] generateCert(@NonNull final Event event, @NonNull final AppUser user){
        final var cert = certificateService.loadPdf(event);

        return replaceTextInPDF("{{UserName}}",user.getName(), cert);
    }

    @SneakyThrows
    public static byte[] replaceTextInPDF( String searchText, String replacementText, byte[] template){
        PDDocument document = PDDocument.load(template);

        for (int pageNumber = 0; pageNumber < document.getNumberOfPages(); pageNumber++) {
            PDFTextStripper stripper = new PDFTextStripper() {
                @Override
                protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
                    StringBuilder builder = new StringBuilder();
                    for (TextPosition text : textPositions) {
                        builder.append(text.getUnicode());
                    }
                    String pageText = builder.toString();
                    pageText = pageText.replace(searchText, replacementText);
                    super.writeString(pageText, textPositions);
                }
            };

            stripper.setStartPage(pageNumber + 1);
            stripper.setEndPage(pageNumber + 1);

            stripper.getText(document);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}
