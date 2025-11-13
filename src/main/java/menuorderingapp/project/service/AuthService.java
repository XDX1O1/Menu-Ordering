package menuorderingapp.project.service;

import menuorderingapp.project.model.Cashier;

public interface AuthService {

    String login(String username, String password);

    void logout(String sessionToken);

    boolean validateSession(String sessionToken);

    Cashier getCashierFromSession(String sessionToken);

    void cleanupExpiredSessions();
}
