package menuorderingapp.project.service.impl;

import menuorderingapp.project.model.Category;
import menuorderingapp.project.model.Menu;
import menuorderingapp.project.repository.CategoryRepository;
import menuorderingapp.project.repository.MenuRepository;
import menuorderingapp.project.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    public MenuServiceImpl(MenuRepository menuRepository, CategoryRepository categoryRepository) {
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Menu> getAvailableMenus() {
        return menuRepository.findByAvailableTrueOrderByName();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Menu> getMenuById(Long id) {
        return menuRepository.findById(id);
    }

    @Override
    public Menu saveMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    @Override
    public Menu updateMenu(Long id, Menu menuDetails) {
        Menu existingMenu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + id));

        existingMenu.setName(menuDetails.getName());
        existingMenu.setDescription(menuDetails.getDescription());
        existingMenu.setPrice(menuDetails.getPrice());
        existingMenu.setImageUrl(menuDetails.getImageUrl());
        existingMenu.setAvailable(menuDetails.getAvailable());
        existingMenu.setIsPromo(menuDetails.getIsPromo());
        existingMenu.setPromoPrice(menuDetails.getPromoPrice());
        existingMenu.setCategory(menuDetails.getCategory());

        return menuRepository.save(existingMenu);
    }

    @Override
    public void deleteMenu(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + id));
        menuRepository.delete(menu);
    }

    @Override
    public Menu toggleMenuAvailability(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + id));
        menu.setAvailable(!menu.getAvailable());
        return menuRepository.save(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Menu> getMenusByCategory(Long categoryId) {
        return menuRepository.findByCategoryIdAndAvailableTrue(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Menu> searchMenus(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAvailableMenus();
        }
        return menuRepository.searchAvailableMenus(searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Menu> getPromoMenus() {
        return menuRepository.findByIsPromoTrueAndAvailableTrue();
    }

    // Category methods
    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category categoryDetails) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        existingCategory.setName(categoryDetails.getName());
        existingCategory.setDisplayOrder(categoryDetails.getDisplayOrder());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesWithMenus() {
        List<Category> categories = getAllCategories();
        // Eagerly fetch menus for each category
        categories.forEach(category ->
                category.setMenus(menuRepository.findByCategoryAndAvailableTrueOrderByName(category)));
        return categories;
    }
}
