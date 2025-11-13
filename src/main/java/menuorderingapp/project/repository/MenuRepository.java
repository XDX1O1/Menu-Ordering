package menuorderingapp.project.repository;

import menuorderingapp.project.model.Category;
import menuorderingapp.project.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByAvailableTrueOrderByName();

    List<Menu> findByCategoryAndAvailableTrueOrderByName(Category category);

    List<Menu> findByCategoryIdAndAvailableTrue(Long categoryId);

    List<Menu> findByIsPromoTrueAndAvailableTrue();

    @Query("SELECT m FROM Menu m WHERE m.available = true " +
            "AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Menu> searchAvailableMenus(@Param("searchTerm") String searchTerm);

    @Query("SELECT m FROM Menu m JOIN FETCH m.category WHERE m.id = :id")
    Optional<Menu> findByIdWithCategory(Long id);

    long countByAvailableTrue();

    long countByAvailableFalse();

    List<Menu> findByAvailableFalse();
}
