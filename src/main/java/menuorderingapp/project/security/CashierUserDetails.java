package menuorderingapp.project.security;

import menuorderingapp.project.model.Cashier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CashierUserDetails implements UserDetails {

    private final Cashier cashier;

    public CashierUserDetails(Cashier cashier) {
        this.cashier = cashier;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public Long getCashierId() {
        return cashier.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(cashier.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return cashier.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return cashier.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return cashier.getIsActive() != null && cashier.getIsActive();
    }
}
