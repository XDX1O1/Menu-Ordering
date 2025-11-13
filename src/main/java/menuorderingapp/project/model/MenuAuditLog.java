package menuorderingapp.project.model;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "menu_audit_log")
public class MenuAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private Cashier cashier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Column(name = "old_values", columnDefinition = "JSON")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "JSON")
    private String newValues;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public MenuAuditLog() {}

    public MenuAuditLog(Menu menu, Cashier cashier, AuditAction action) {
        this.menu = menu;
        this.cashier = cashier;
        this.action = action;
    }

    public MenuAuditLog(Menu menu, Cashier cashier, AuditAction action, String oldValues, String newValues) {
        this.menu = menu;
        this.cashier = cashier;
        this.action = action;
        this.oldValues = oldValues;
        this.newValues = newValues;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }

    public Cashier getCashier() { return cashier; }
    public void setCashier(Cashier cashier) { this.cashier = cashier; }

    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }

    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }

    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public enum AuditAction {
        CREATED, UPDATED, PRICE_CHANGED, AVAILABILITY_CHANGED, DELETED
    }

    @Override
    public String toString() {
        return "MenuAuditLog{" +
                "id=" + id +
                ", menu=" + (menu != null ? menu.getName() : "null") +
                ", cashier=" + (cashier != null ? cashier.getUsername() : "null") +
                ", action=" + action +
                ", createdAt=" + createdAt +
                '}';
    }
}
