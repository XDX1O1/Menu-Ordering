package menuorderingapp.project.model.dto;

import menuorderingapp.project.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private OrderResponse order;
    private CashierDto cashier;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;
    private Order.PaymentMethod paymentMethod;
    private LocalDateTime createdAt;


    public InvoiceResponse() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public OrderResponse getOrder() {
        return order;
    }

    public void setOrder(OrderResponse order) {
        this.order = order;
    }

    public CashierDto getCashier() {
        return cashier;
    }

    public void setCashier(CashierDto cashier) {
        this.cashier = cashier;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Order.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Order.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
