package menuorderingapp.project.service;

import menuorderingapp.project.model.Category;
import menuorderingapp.project.model.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuService {

    List<Menu> getAllMenus();

    List<Menu> getAvailableMenus();

    Optional<Menu> getMenuById(Long id);

    Menu saveMenu(Menu menu);

    Menu updateMenu(Long id, Menu menuDetails);

    void deleteMenu(Long id);

    Menu toggleMenuAvailability(Long id);

    List<Menu> getMenusByCategory(Long categoryId);

    List<Menu> searchMenus(String searchTerm);

    List<Menu> getPromoMenus();

    List<Category> getAllCategories();

    Optional<Category> getCategoryById(Long id);

    Category saveCategory(Category category);

    Category updateCategory(Long id, Category categoryDetails);

    void deleteCategory(Long id);

    List<Category> getCategoriesWithMenus();
}
