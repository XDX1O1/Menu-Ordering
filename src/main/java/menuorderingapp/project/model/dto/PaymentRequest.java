package menuorderingapp.project.model.dto;

import jakarta.validation.constraints.NotNull;
import menuorderingapp.project.model.Order;

public class PaymentRequest {

    @NotNull(message = "Order number is required")
    private String orderNumber;

    @NotNull(message = "Payment method is required")
    private Order.PaymentMethod paymentMethod;

    private Double cashAmount; // For cash payments
    private String qrData; // For QR payments

    // Constructors
    public PaymentRequest() {
    }

    public PaymentRequest(String orderNumber, Order.PaymentMethod paymentMethod) {
        this.orderNumber = orderNumber;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Order.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Order.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }
}
