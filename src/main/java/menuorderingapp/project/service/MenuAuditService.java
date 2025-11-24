package menuorderingapp.project.service;

import menuorderingapp.project.model.Cashier;
import menuorderingapp.project.model.Menu;
import menuorderingapp.project.model.MenuAuditLog;

import java.time.LocalDateTime;
import java.util.List;

public interface MenuAuditService {

    void logMenuCreate(Menu menu, Cashier cashier);

    void logMenuUpdate(Menu menu, Cashier cashier, String oldValues, String newValues);

    void logMenuDelete(Menu menu, Cashier cashier);

    void logAvailabilityChange(Menu menu, Cashier cashier, boolean oldValue, boolean newValue);

    void logPriceChange(Menu menu, Cashier cashier, double oldPrice, double newPrice);

    List<MenuAuditLog> getMenuAuditLogs(Menu menu);

    List<MenuAuditLog> getCashierAuditLogs(Cashier cashier);

    List<MenuAuditLog> getRecentAuditLogs(int limit);

    List<MenuAuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end);
}