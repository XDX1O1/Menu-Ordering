package menuorderingapp.project.exception;

public class CashierNotFoundException extends RuntimeException{
    public CashierNotFoundException(String message) {
        super(message);
    }

    public CashierNotFoundException(Long id) {
        super("Cashier not found with id: " + id);
    }
}
