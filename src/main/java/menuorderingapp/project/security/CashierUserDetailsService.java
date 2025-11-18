package menuorderingapp.project.security;

import menuorderingapp.project.model.Cashier;
import menuorderingapp.project.repository.CashierRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CashierUserDetailsService implements UserDetailsService {

    private final CashierRepository cashierRepository;

    public CashierUserDetailsService(CashierRepository cashierRepository) {
        this.cashierRepository = cashierRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Cashier cashier = cashierRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new CashierUserDetails(cashier);
    }
}
