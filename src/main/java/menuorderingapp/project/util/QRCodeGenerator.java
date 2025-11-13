package menuorderingapp.project.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class QRCodeGenerator {

    public String generateQRCodeImage(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE,
                Constants.QR_CODE_WIDTH, Constants.QR_CODE_HEIGHT);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, Constants.QR_CODE_FORMAT, outputStream);

        byte[] qrCodeBytes = outputStream.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);
    }

    public String generatePaymentQRCode(String orderNumber, Double amount) {
        String paymentData = String.format(
                "order_number=%s&amount=%.2f&merchant=%s&currency=%s",
                orderNumber, amount, Constants.APP_NAME, Constants.CURRENCY
        );

        try {
            return generateQRCodeImage(paymentData);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}
