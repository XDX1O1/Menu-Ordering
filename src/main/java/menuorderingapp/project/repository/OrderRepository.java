package menuorderingapp.project.repository;

import menuorderingapp.project.model.Cashier;
import menuorderingapp.project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status);

    List<Order> findByOrderTypeOrderByCreatedAtDesc(Order.OrderType orderType);

    List<Order> findByPaymentStatusOrderByCreatedAtDesc(Order.PaymentStatus paymentStatus);

    List<Order> findByCashierOrderByCreatedAtDesc(Cashier cashier);

    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt < :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.paymentStatus = 'PAID' AND o.createdAt BETWEEN :start AND :end")
    List<Order> findPaidOrdersBetween(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(Order.OrderStatus status);

    @Query("SELECT SUM(o.total) FROM Order o WHERE o.paymentStatus = 'PAID' AND o.createdAt BETWEEN :start AND :end")
    Double getTotalRevenueBetween(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    List<Order> findByCustomerNameContainingIgnoreCaseOrderByCreatedAtDesc(String customerName);
}
