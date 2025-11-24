package menuorderingapp.project.model.dto;

import menuorderingapp.project.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private String orderNumber;
    private BigDecimal total;
    private Order.OrderStatus status;
    private Order.OrderType orderType;
    private Order.PaymentMethod paymentMethod;
    private Order.PaymentStatus paymentStatus;
    private String customerName;
    private CashierDto cashier;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public OrderResponse() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }

    public Order.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(Order.OrderType orderType) {
        this.orderType = orderType;
    }

    public Order.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Order.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Order.PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Order.PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public CashierDto getCashier() {
        return cashier;
    }

    public void setCashier(CashierDto cashier) {
        this.cashier = cashier;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
