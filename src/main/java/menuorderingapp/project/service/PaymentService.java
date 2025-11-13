package menuorderingapp.project.service;

import menuorderingapp.project.model.Order;

public interface PaymentService {

    String generateQRCode(String paymentData);

    boolean processQRPayment(String orderNumber, String qrData);

    boolean processCashPayment(String orderNumber, Double amountTendered);

    String generatePaymentQRCode(Order order);

    boolean verifyPayment(String orderNumber);
}
