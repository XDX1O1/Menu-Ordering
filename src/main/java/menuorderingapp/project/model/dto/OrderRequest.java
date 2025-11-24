package menuorderingapp.project.model.dto;

import jakarta.validation.constraints.NotNull;
import menuorderingapp.project.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRequest {

    @NotNull(message = "Order type is required")
    private Order.OrderType orderType;

    private String customerName;

    private Long cashierId; // For cashier-assisted orders

    private List<OrderItemRequest> items = new ArrayList<>();


    public OrderRequest() {
    }

    public OrderRequest(Order.OrderType orderType, String customerName) {
        this.orderType = orderType;
        this.customerName = customerName;
    }


    public Order.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(Order.OrderType orderType) {
        this.orderType = orderType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getCashierId() {
        return cashierId;
    }

    public void setCashierId(Long cashierId) {
        this.cashierId = cashierId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
