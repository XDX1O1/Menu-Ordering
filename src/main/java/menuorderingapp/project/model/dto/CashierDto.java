package menuorderingapp.project.model.dto;

import menuorderingapp.project.model.Cashier;

import java.time.LocalDateTime;

public class CashierDto {

    private Long id;
    private String username;
    private String displayName;
    private Cashier.CashierRole role;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;


    public CashierDto() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Cashier.CashierRole getRole() {
        return role;
    }

    public void setRole(Cashier.CashierRole role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
