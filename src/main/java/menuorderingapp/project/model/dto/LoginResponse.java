package menuorderingapp.project.model.dto;

public class LoginResponse {

    private String sessionToken;
    private CashierDto cashier;
    private String message;


    public LoginResponse() {
    }

    public LoginResponse(String sessionToken, CashierDto cashier, String message) {
        this.sessionToken = sessionToken;
        this.cashier = cashier;
        this.message = message;
    }


    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public CashierDto getCashier() {
        return cashier;
    }

    public void setCashier(CashierDto cashier) {
        this.cashier = cashier;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
