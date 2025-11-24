package menuorderingapp.project.model.dto;

public class PaymentResponse {

    private boolean success;
    private String message;
    private String orderNumber;
    private String qrCodeImage; // Base64 encoded QR code
    private Double change; // For cash payments


    public PaymentResponse() {
    }

    public PaymentResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getQrCodeImage() {
        return qrCodeImage;
    }

    public void setQrCodeImage(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }
}
