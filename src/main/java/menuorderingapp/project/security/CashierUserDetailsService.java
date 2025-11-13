package menuorderingapp.project.security;

import menuorderingapp.project.model.Cashier;
import menuorderingapp.project.repository.CashierRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CashierUserDetailsService implements UserDetailsService {

    private final CashierRepository cashierRepository;

    public CashierUserDetailsService(CashierRepository cashierRepository) {
        this.cashierRepository = cashierRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Cashier> cashierOpt = cashierRepository.findByUsernameAndIsActiveTrue(username);

        if (cashierOpt.isEmpty()) {
            throw new UsernameNotFoundException("Cashier not found with username: " + username);
        }

        return new CashierUserDetails(cashierOpt.get());
    }
}
