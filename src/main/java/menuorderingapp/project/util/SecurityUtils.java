package menuorderingapp.project.util;

import menuorderingapp.project.security.CashierUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static CashierUserDetails getCurrentCashier() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CashierUserDetails) {
            return (CashierUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public static Long getCurrentCashierId() {
        CashierUserDetails cashierDetails = getCurrentCashier();
        return cashierDetails != null ? cashierDetails.getCashierId() : null;
    }

    public static String getCurrentCashierUsername() {
        CashierUserDetails cashierDetails = getCurrentCashier();
        return cashierDetails != null ? cashierDetails.getUsername() : null;
    }

    public static boolean isAuthenticated() {
        return getCurrentCashier() != null;
    }

    public static boolean hasRole(String role) {
        CashierUserDetails cashierDetails = getCurrentCashier();
        if (cashierDetails == null) {
            return false;
        }
        return cashierDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }
}
