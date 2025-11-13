package menuorderingapp.project.repository;

import menuorderingapp.project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findAllByOrderByDisplayOrderAsc();

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.menus WHERE c.id = :id")
    Optional<Category> findByIdWithMenus(Long id);

    boolean existsByName(String name);
}
