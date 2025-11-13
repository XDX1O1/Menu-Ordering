package menuorderingapp.project.service.impl;

import menuorderingapp.project.model.Cashier;
import menuorderingapp.project.model.CashierSession;
import menuorderingapp.project.repository.CashierRepository;
import menuorderingapp.project.repository.CashierSessionRepository;
import menuorderingapp.project.security.CashierUserDetails;
import menuorderingapp.project.service.AuthService;
import menuorderingapp.project.service.CashierService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final CashierService cashierService;
    private final CashierRepository cashierRepository;
    private final CashierSessionRepository sessionRepository;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(CashierService cashierService,
                           CashierRepository cashierRepository,
                           CashierSessionRepository sessionRepository,
                           AuthenticationManager authenticationManager) {
        this.cashierService = cashierService;
        this.cashierRepository = cashierRepository;
        this.sessionRepository = sessionRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public String login(String username, String password) {
        try {
            // Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CashierUserDetails userDetails = (CashierUserDetails) authentication.getPrincipal();
            Cashier cashier = userDetails.getCashier();

            // Update last login
            cashierService.updateLastLogin(cashier.getId());

            // Create session token (for API calls)
            String sessionToken = generateSessionToken();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(8);

            CashierSession session = new CashierSession(cashier, sessionToken, expiresAt);
            sessionRepository.save(session);

            return sessionToken;

        } catch (Exception e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    @Override
    public void logout(String sessionToken) {
        // Clear Spring Security context
        SecurityContextHolder.clearContext();

        // Remove session from database
        Optional<CashierSession> sessionOpt = sessionRepository.findBySessionToken(sessionToken);
        sessionOpt.ifPresent(sessionRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateSession(String sessionToken) {
        Optional<CashierSession> sessionOpt = sessionRepository.findBySessionToken(sessionToken);
        if (sessionOpt.isEmpty()) {
            return false;
        }

        CashierSession session = sessionOpt.get();
        if (session.isExpired()) {
            sessionRepository.delete(session);
            return false;
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Cashier getCashierFromSession(String sessionToken) {
        Optional<CashierSession> sessionOpt = sessionRepository.findBySessionToken(sessionToken);
        if (sessionOpt.isEmpty() || sessionOpt.get().isExpired()) {
            throw new RuntimeException("Invalid or expired session");
        }

        return sessionOpt.get().getCashier();
    }

    @Override
    public void cleanupExpiredSessions() {
        sessionRepository.deleteExpiredSessions(LocalDateTime.now());
    }

    private String generateSessionToken() {
        return UUID.randomUUID().toString();
    }
}
