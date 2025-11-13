package menuorderingapp.project.exception;

public class MenuNotFoundException extends RuntimeException{
    public MenuNotFoundException(String message) {
        super(message);
    }

    public MenuNotFoundException(Long id) {
        super("Menu not found with id: " + id);
    }
}
