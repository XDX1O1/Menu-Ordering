package menuorderingapp.project.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MenuResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean available;
    private Boolean isPromo;
    private BigDecimal promoPrice;
    private BigDecimal currentPrice;
    private CategoryResponse category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public MenuResponse() {}


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }

    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getAvailable() { return available; }

    public void setAvailable(Boolean available) { this.available = available; }

    public Boolean getIsPromo() { return isPromo; }

    public void setIsPromo(Boolean isPromo) { this.isPromo = isPromo; }

    public BigDecimal getPromoPrice() { return promoPrice; }

    public void setPromoPrice(BigDecimal promoPrice) { this.promoPrice = promoPrice; }

    public BigDecimal getCurrentPrice() { return currentPrice; }

    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public CategoryResponse getCategory() { return category; }

    public void setCategory(CategoryResponse category) { this.category = category; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
