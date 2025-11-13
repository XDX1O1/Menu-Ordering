package menuorderingapp.project.model.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class SalesReportResponse {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double totalRevenue;
    private Long totalOrders;
    private Double averageOrderValue;
    private Map<String, Double> revenueByPaymentMethod;
    private String generatedAt;

    // Constructors
    public SalesReportResponse() {
    }

    // Getters and Setters
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(Double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public Map<String, Double> getRevenueByPaymentMethod() {
        return revenueByPaymentMethod;
    }

    public void setRevenueByPaymentMethod(Map<String, Double> revenueByPaymentMethod) {
        this.revenueByPaymentMethod = revenueByPaymentMethod;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}
