package menuorderingapp.project.service.impl;

import menuorderingapp.project.model.Cashier;
import menuorderingapp.project.repository.CashierRepository;
import menuorderingapp.project.service.CashierService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CashierServiceImpl implements CashierService {

    private final CashierRepository cashierRepository;
    private final PasswordEncoder passwordEncoder;

    public CashierServiceImpl(CashierRepository cashierRepository, PasswordEncoder passwordEncoder) {
        this.cashierRepository = cashierRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Cashier createCashier(Cashier cashier) {
        if (cashierRepository.existsByUsername(cashier.getUsername())) {
            throw new RuntimeException("Username already exists: " + cashier.getUsername());
        }

        cashier.setPasswordHash(passwordEncoder.encode(cashier.getPasswordHash()));
        cashier.setIsActive(true);

        return cashierRepository.save(cashier);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cashier> getCashierById(Long id) {
        return cashierRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cashier> getCashierByUsername(String username) {
        return cashierRepository.findByUsernameAndIsActiveTrue(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cashier> getAllCashiers() {
        return cashierRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cashier> getActiveCashiers() {
        return cashierRepository.findByIsActiveTrue();
    }

    @Override
    public Cashier updateCashier(Long id, Cashier cashierDetails) {
        Cashier existingCashier = cashierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cashier not found with id: " + id));

        if (!existingCashier.getUsername().equals(cashierDetails.getUsername()) &&
                cashierRepository.existsByUsername(cashierDetails.getUsername())) {
            throw new RuntimeException("Username already exists: " + cashierDetails.getUsername());
        }

        existingCashier.setUsername(cashierDetails.getUsername());
        existingCashier.setDisplayName(cashierDetails.getDisplayName());
        existingCashier.setRole(cashierDetails.getRole());

        if (cashierDetails.getPasswordHash() != null && !cashierDetails.getPasswordHash().isEmpty()) {
            existingCashier.setPasswordHash(passwordEncoder.encode(cashierDetails.getPasswordHash()));
        }

        return cashierRepository.save(existingCashier);
    }

    @Override
    public void deactivateCashier(Long id) {
        Cashier cashier = cashierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cashier not found with id: " + id));
        cashier.setIsActive(false);
        cashierRepository.save(cashier);
    }

    @Override
    public void activateCashier(Long id) {
        Cashier cashier = cashierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cashier not found with id: " + id));
        cashier.setIsActive(true);
        cashierRepository.save(cashier);
    }

    @Override
    public boolean validateCashierCredentials(String username, String password) {
        Optional<Cashier> cashierOpt = cashierRepository.findByUsernameAndIsActiveTrue(username);
        if (cashierOpt.isEmpty()) {
            return false;
        }

        Cashier cashier = cashierOpt.get();
        return passwordEncoder.matches(password, cashier.getPasswordHash());
    }

    @Override
    public void updateLastLogin(Long cashierId) {
        Cashier cashier = cashierRepository.findById(cashierId)
                .orElseThrow(() -> new RuntimeException("Cashier not found with id: " + cashierId));
        cashier.setLastLogin(LocalDateTime.now());
        cashierRepository.save(cashier);
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveCashiersCount() {
        return cashierRepository.countByIsActiveTrue();
    }
}
