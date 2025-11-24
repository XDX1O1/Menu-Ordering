package menuorderingapp.project.repository;

import menuorderingapp.project.model.Cashier;
import menuorderingapp.project.model.Invoice;
import menuorderingapp.project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByOrder(Order order);

    List<Invoice> findByCashierOrderByCreatedAtDesc(Cashier cashier);

    List<Invoice> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT i FROM Invoice i JOIN FETCH i.order o JOIN FETCH o.orderItems LEFT JOIN FETCH i.cashier WHERE i.createdAt BETWEEN :startDate AND :endDate ORDER BY i.createdAt DESC")
    List<Invoice> findInvoicesByDateRange(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(i.finalAmount) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end")
    Double getTotalInvoiceAmountBetween(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("SELECT i FROM Invoice i JOIN FETCH i.order LEFT JOIN FETCH i.cashier WHERE i.id = :id")
    Optional<Invoice> findByIdWithOrderAndCashier(Long id);
}
