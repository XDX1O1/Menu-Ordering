package menuorderingapp.project.repository;

import menuorderingapp.project.model.Cashier;
import menuorderingapp.project.model.CashierSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashierSessionRepository extends JpaRepository<CashierSession, Long> {

    Optional<CashierSession> findBySessionToken(String sessionToken);

    List<CashierSession> findByCashier(Cashier cashier);

    @Query("SELECT cs FROM CashierSession cs WHERE cs.expiresAt < :now")
    List<CashierSession> findExpiredSessions(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM CashierSession cs WHERE cs.expiresAt < :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM CashierSession cs WHERE cs.cashier = :cashier")
    void deleteByCashier(@Param("cashier") Cashier cashier);

    boolean existsBySessionToken(String sessionToken);
}
