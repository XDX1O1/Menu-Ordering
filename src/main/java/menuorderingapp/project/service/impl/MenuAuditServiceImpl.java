package menuorderingapp.project.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import menuorderingapp.project.model.Cashier;
import menuorderingapp.project.model.Menu;
import menuorderingapp.project.model.MenuAuditLog;
import menuorderingapp.project.repository.MenuAuditLogRepository;
import menuorderingapp.project.service.MenuAuditService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MenuAuditServiceImpl implements MenuAuditService {

    private final MenuAuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public MenuAuditServiceImpl(MenuAuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void logMenuCreate(Menu menu, Cashier cashier) {
        MenuAuditLog log = new MenuAuditLog(menu, cashier, MenuAuditLog.AuditAction.CREATED);
        log.setMenuName(menu.getName());
        log.setNewValues(menuToJson(menu));
        auditLogRepository.save(log);
    }

    @Override
    public void logMenuUpdate(Menu menu, Cashier cashier, String oldValues, String newValues) {
        MenuAuditLog log = new MenuAuditLog(menu, cashier, MenuAuditLog.AuditAction.UPDATED, oldValues, newValues);
        log.setMenuName(menu.getName());
        auditLogRepository.save(log);
    }

    @Override
    public void logMenuDelete(Menu menu, Cashier cashier) {
        MenuAuditLog log = new MenuAuditLog();
        log.setMenu(null);
        log.setMenuName(menu.getName());
        log.setCashier(cashier);
        log.setAction(MenuAuditLog.AuditAction.DELETED);
        log.setOldValues(menuToJson(menu));
        auditLogRepository.save(log);
    }

    @Override
    public void logAvailabilityChange(Menu menu, Cashier cashier, boolean oldValue, boolean newValue) {
        MenuAuditLog log = new MenuAuditLog(menu, cashier, MenuAuditLog.AuditAction.AVAILABILITY_CHANGED);
        log.setMenuName(menu.getName());
        log.setOldValues(String.format("{\"available\":%b}", oldValue));
        log.setNewValues(String.format("{\"available\":%b}", newValue));
        auditLogRepository.save(log);
    }

    @Override
    public void logPriceChange(Menu menu, Cashier cashier, double oldPrice, double newPrice) {
        MenuAuditLog log = new MenuAuditLog(menu, cashier, MenuAuditLog.AuditAction.PRICE_CHANGED);
        log.setMenuName(menu.getName());
        log.setOldValues(String.format("{\"price\":%.2f}", oldPrice));
        log.setNewValues(String.format("{\"price\":%.2f}", newPrice));
        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuAuditLog> getMenuAuditLogs(Menu menu) {
        return auditLogRepository.findByMenuOrderByCreatedAtDesc(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuAuditLog> getCashierAuditLogs(Cashier cashier) {
        return auditLogRepository.findByCashierOrderByCreatedAtDesc(cashier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuAuditLog> getRecentAuditLogs(int limit) {
        return auditLogRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuAuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByCreatedAtBetween(start, end);
    }

    private String menuToJson(Menu menu) {
        try {
            Map<String, Object> menuData = new HashMap<>();
            menuData.put("name", menu.getName());
            menuData.put("description", menu.getDescription());
            menuData.put("price", menu.getPrice());
            menuData.put("category", menu.getCategory() != null ? menu.getCategory().getName() : null);
            menuData.put("available", menu.getAvailable());
            menuData.put("isPromo", menu.getIsPromo());
            menuData.put("promoPrice", menu.getPromoPrice());
            return objectMapper.writeValueAsString(menuData);
        } catch (Exception e) {
            return "{}";
        }
    }
}
