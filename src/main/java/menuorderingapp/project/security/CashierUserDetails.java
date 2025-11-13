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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + cashier.getRole().name()));
    }

    @Override
    public String getPassword() {
        String pwd = cashier.getPasswordHash();
        System.out.println("DEBUG: Retrieved password hash: " + pwd);
        System.out.println("DEBUG: Hash length: " + (pwd != null ? pwd.length() : "NULL"));
        return pwd;
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
        return cashier.getIsActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return cashier.getIsActive();
    }

    public Cashier getCashier() {
        return cashier;
    }

    public Long getCashierId() {
        return cashier.getId();
    }

    public String getDisplayName() {
        return cashier.getDisplayName();
    }
}
