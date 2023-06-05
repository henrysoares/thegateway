package com.meteorinc.thegateway.interfaces.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meteorinc.thegateway.application.GatewayEventFacade;
import com.meteorinc.thegateway.application.email.EmailService;
import com.meteorinc.thegateway.domain.event.EventDTO;
import com.meteorinc.thegateway.domain.event.EventStatus;
import com.meteorinc.thegateway.interfaces.event.dto.EventCreationResponse;
import com.meteorinc.thegateway.interfaces.event.requests.EventDetailsRequest;
import com.meteorinc.thegateway.interfaces.event.requests.EventUserStateValidation;
import com.meteorinc.thegateway.interfaces.event.requests.FireEmailsRequest;
import com.opencsv.CSVWriter;
import java.io.*;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/gateway/event")
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EventResource {

    GatewayEventFacade gatewayEventFacade;

    EmailService emailService;

  private static final String DEST = "C:\\Users\\henry\\OneDrive\\Área de Trabalho\\benio";

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
    public ResponseEntity<Void> doCheckIn (
            @NonNull @RequestHeader("Authorization") final String token,
            @PathVariable("eventCode") UUID eventCode,
            @RequestBody @NonNull final EventUserStateValidation request){
        gatewayEventFacade.doCheckIn(token, eventCode, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/check-out/{eventCode}")
    public ResponseEntity<Void> doCheckOut (
            @NonNull @RequestHeader("Authorization") final String token,
            @PathVariable("eventCode") UUID eventCode){
        gatewayEventFacade.doCheckOut(token, eventCode);
        return ResponseEntity.ok().build();
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
        emailService.sendEmail(request.getDummyEmail(),request.getDummySubject(), "Teste");
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
        gatewayEventFacade.startEvent(eventCode, EventStatus.IN_PROGRESS);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{eventCode}/finish")
    public ResponseEntity<Void> finishEvent(@PathVariable("eventCode") UUID eventCode) {
        gatewayEventFacade.finishEvent(eventCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateCertificate() throws IOException {

        // Carregar o template do certificado a partir do banco de dados ou de outro local
        byte[] templateBytes = getTemplateFromDatabase();
        PDDocument document = PDDocument.load(templateBytes);

        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        text = text.replace("{{UserName}}", "HENRY SOARES").replace("\r", "").replace("\n","");

        PDDocument newDocument = new PDDocument();
        PDPage page = new PDPage();
        newDocument.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(newDocument, page);
        contentStream.beginText();

       // PDType0Font font = PDType0Font.load(document, new File("fonts/Arial-BoldMT.ttf"));
        contentStream.setFont(PDType1Font.COURIER, 12);
        //contentStream.setFont(, 12);

        contentStream.newLineAtOffset(100, 700);
        contentStream.showText(text);
        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newDocument.save(baos);
        byte[] bytes = baos.toByteArray();


        contentStream.close();
        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + "teste.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        // Retorna a resposta HTTP com o arquivo PDF
        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }


    private byte[] getTemplateFromDatabase() {
        return gatewayEventFacade.loadCert(UUID.fromString("25947b7e-f165-4034-9c81-03d730621060"));
    }
}





