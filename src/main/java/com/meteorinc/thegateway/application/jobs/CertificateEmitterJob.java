package com.meteorinc.thegateway.application.jobs;

import com.meteorinc.thegateway.application.email.EmailService;
import com.meteorinc.thegateway.application.event.CertificateService;
import com.meteorinc.thegateway.domain.checkin.CheckIn;
import com.meteorinc.thegateway.domain.checkin.CheckInCertificateStatus;
import com.meteorinc.thegateway.domain.checkin.CheckInRepository;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.domain.user.AppUser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
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

   @Scheduled(fixedDelay = 20000)
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
                        generateAndSaveCert(checkIn);
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
    public void generateAndSaveCert(@NonNull final CheckIn checkIn){
        byte[] pdfBytes = certificateService.loadPdf(checkIn.getEvent());

        PDDocument document = PDDocument.load(pdfBytes);

        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        text = text.replace("{{UserName}}", checkIn.getAppUser().getName())
                .replace("\r", "").replace("\n","");

        PDDocument newDocument = new PDDocument();
        PDPage page = new PDPage();
        newDocument.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(newDocument, page);
        contentStream.beginText();

        contentStream.setFont(PDType1Font.COURIER, 12);

        contentStream.newLineAtOffset(100, 700);
        contentStream.showText(text);
        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newDocument.save(baos);
        byte[] bytes = baos.toByteArray();

        contentStream.close();
        document.close();

        checkIn.setGeneratedCertificate(bytes);
        checkIn.setCertificateStatus(CheckInCertificateStatus.DONE);
        checkInRepository.save(checkIn);

        document.close();
    }

    /*
    @SneakyThrows
    private byte[] generateCert(@NonNull final Event event, @NonNull final AppUser user){
        final var cert = certificateService.loadPdf(event);

        return replaceTextInPDF("{{UserName}}",user.getName(), cert);
    }

    @SneakyThrows
    public static byte[] replaceTextInPDF( String searchText, String replacementText, byte[] template){

        // Carregar PDF usando o PDFBox
        PDDocument document = PDDocument.load(new ByteArrayInputStream(template));

        // Realizar a substituição da palavra
        PDFTextStripper stripper = new PDFTextStripper();
        String content = stripper.getText(document);
        String newContent = content.replace("{{UserName}}", replacementText);

        // Converter o conteúdo de volta para bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        byte[] modifiedPdfBytes = outputStream.toByteArray();

        // Salvar os bytes modificados no banco de dados
        certificate.setPdfBytes(modifiedPdfBytes);
        certificateRepository.save(certificate);

        // Fechar o documento
        document.close();
    }*/
}
