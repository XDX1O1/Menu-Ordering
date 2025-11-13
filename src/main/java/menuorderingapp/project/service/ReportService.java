package menuorderingapp.project.service;

import menuorderingapp.project.model.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportService {

    Map<String, Object> getSalesReport(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Object> getDailySalesReport(LocalDate date);

    List<Map<String, Object>> getTopSellingItems(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Object> getCashierPerformanceReport(LocalDateTime startDate, LocalDateTime endDate);

    List<Order> getOrdersForReport(LocalDateTime startDate, LocalDateTime endDate);
}
