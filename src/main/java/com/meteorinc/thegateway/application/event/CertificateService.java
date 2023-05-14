package com.meteorinc.thegateway.application.event;

import com.meteorinc.thegateway.domain.event.Certificate;
import com.meteorinc.thegateway.domain.event.CertificateRepository;
import com.meteorinc.thegateway.domain.event.Event;
import com.meteorinc.thegateway.interfaces.event.requests.CertifiedUploadRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CertificateService {

    CertificateRepository certificateRepository;

    public void saveCertificate(@NonNull final MultipartFile file,
                                @NonNull final Event event,
                                @NonNull final CertifiedUploadRequest request){
        try {

            final Certificate cert = Certificate.builder()
                    .metadata(request.getMetadata().asText())
                    .certificateCode(UUID.randomUUID())
                    .description(request.getDescription())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .content(file.getBytes())
                    .event(event)
                    .build();

            certificateRepository.save(cert);

            log.info("certificate persisted.");
        }catch (Exception exception){
            log.error("Was not possible to persist the certificate.", exception);
        }
    }

    public byte[] loadPdf(@NonNull final Event event) throws IOException {
        final var certificate = event.getCertificate();

        return certificate.getContent();
    }
}
