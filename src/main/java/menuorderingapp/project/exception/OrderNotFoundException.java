package menuorderingapp.project.exception;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(Long id) {
        super("Order not found with id: " + id);
    }

    public OrderNotFoundException(String orderNumber, String message) {
        super("Order not found with number: " + orderNumber + ". " + message);
    }
}
