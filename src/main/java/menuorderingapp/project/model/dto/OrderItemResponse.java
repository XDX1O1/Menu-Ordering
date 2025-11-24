package menuorderingapp.project.model.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long id;
    private MenuResponse menu;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;


    public OrderItemResponse() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MenuResponse getMenu() {
        return menu;
    }

    public void setMenu(MenuResponse menu) {
        this.menu = menu;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
