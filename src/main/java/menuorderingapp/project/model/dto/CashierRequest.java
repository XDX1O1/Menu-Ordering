package menuorderingapp.project.model.dto;

import jakarta.validation.constraints.NotBlank;
import menuorderingapp.project.model.Cashier;

public class CashierRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private Cashier.CashierRole role = Cashier.CashierRole.CASHIER;


    public CashierRequest() {
    }

    public CashierRequest(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
