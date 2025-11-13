package menuorderingapp.project.repository;

import menuorderingapp.project.model.Cashier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashierRepository extends JpaRepository<Cashier, Long> {

    Optional<Cashier> findByUsername(String username);

    Optional<Cashier> findByUsernameAndIsActiveTrue(String username);

    List<Cashier> findByIsActiveTrue();

    List<Cashier> findByIsActiveFalse();

    List<Cashier> findByRole(Cashier.CashierRole role);

    @Query("SELECT c FROM Cashier c WHERE c.isActive = true AND c.role = 'ADMIN'")
    List<Cashier> findActiveAdmins();

    boolean existsByUsername(String username);

    long countByIsActiveTrue();
}
