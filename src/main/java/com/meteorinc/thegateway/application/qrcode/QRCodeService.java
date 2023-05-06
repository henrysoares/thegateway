package com.meteorinc.thegateway.application.qrcode;

import lombok.NonNull;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QRCodeService {

    public byte[] generateQRCode(@NonNull final UUID eventCode){
        return QRCode.from(eventCode.toString()).withSize(200, 200).to(ImageType.PNG).stream().toByteArray();
    }



}
