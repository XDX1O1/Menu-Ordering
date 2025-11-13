package menuorderingapp.project.service;

import menuorderingapp.project.model.Cashier;

import java.util.List;
import java.util.Optional;

public interface CashierService {

    Cashier createCashier(Cashier cashier);

    Optional<Cashier> getCashierById(Long id);

    Optional<Cashier> getCashierByUsername(String username);

    List<Cashier> getAllCashiers();

    List<Cashier> getActiveCashiers();

    Cashier updateCashier(Long id, Cashier cashierDetails);

    void deactivateCashier(Long id);

    void activateCashier(Long id);

    boolean validateCashierCredentials(String username, String password);

    void updateLastLogin(Long cashierId);

    long getActiveCashiersCount();
}
