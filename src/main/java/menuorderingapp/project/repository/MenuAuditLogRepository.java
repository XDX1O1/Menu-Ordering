package menuorderingapp.project.repository;

import menuorderingapp.project.model.Cashier;
import menuorderingapp.project.model.Menu;
import menuorderingapp.project.model.MenuAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MenuAuditLogRepository extends JpaRepository<MenuAuditLog, Long> {

    List<MenuAuditLog> findByMenuOrderByCreatedAtDesc(Menu menu);

    List<MenuAuditLog> findByCashierOrderByCreatedAtDesc(Cashier cashier);

    List<MenuAuditLog> findByActionOrderByCreatedAtDesc(MenuAuditLog.AuditAction action);

    List<MenuAuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT mal FROM MenuAuditLog mal WHERE mal.menu = :menu AND mal.createdAt BETWEEN :start AND :end ORDER BY mal.createdAt DESC")
    List<MenuAuditLog> findMenuAuditsByDateRange(@Param("menu") Menu menu,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(mal) FROM MenuAuditLog mal WHERE mal.cashier = :cashier AND mal.createdAt BETWEEN :start AND :end")
    long countByCashierAndDateRange(@Param("cashier") Cashier cashier,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);
}
