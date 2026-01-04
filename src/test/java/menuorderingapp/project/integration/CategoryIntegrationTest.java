package menuorderingapp.project.integration;

import menuorderingapp.project.MenuOrderingAppApplication;
import menuorderingapp.project.model.Category;
import menuorderingapp.project.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MenuOrderingAppApplication.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("Category Integration Tests")
class CategoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category beveragesCategory;
    private Category dessertsCategory;

    @BeforeEach
    void setUp() {
        // Setup test data
        beveragesCategory = new Category("Beverages", 1);
        dessertsCategory = new Category("Desserts", 2);
    }

    /**
     * Test Case 01 (TC01) - Integration: Persist category with valid data
     * Verifies that category is actually saved to database
     */
    @Test
    @DisplayName("TC01 Integration: Should persist category to database with valid data")
    void testSaveCategory_WithValidData_ShouldPersist() {
        // Act
        Category savedCategory = categoryRepository.save(beveragesCategory);

        // Assert
        assertThat(savedCategory.getId()).isNotNull();

        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Beverages");
        assertThat(foundCategory.get().getDisplayOrder()).isEqualTo(1);
        assertThat(foundCategory.get().getCreatedAt()).isNotNull();
        assertThat(foundCategory.get().getUpdatedAt()).isNotNull();
    }

    /**
     * Test Case 03 (TC03) - Integration: Delete category from database
     * Verifies that deleted category is removed from the database
     */
    @Test
    @DisplayName("TC03 Integration: Should delete category from database")
    void testDeleteCategory_ShouldRemoveFromDatabase() {
        // Arrange - Save a category first
        Category savedCategory = categoryRepository.save(beveragesCategory);
        Long categoryId = savedCategory.getId();

        // Act - Delete the category
        categoryRepository.delete(savedCategory);

        // Assert - Verify it's deleted
        Optional<Category> deletedCategory = categoryRepository.findById(categoryId);
        assertThat(deletedCategory).isEmpty();
    }

    /**
     * Test Case 03 (TC03) - Integration: Verify deleted category not in list
     */
    @Test
    @DisplayName("TC03 Integration: Deleted category should not appear in category list")
    void testDeleteCategory_ShouldNotAppearInList() {
        // Arrange - Save two categories
        categoryRepository.save(beveragesCategory);
        Category savedDesserts = categoryRepository.save(dessertsCategory);

        // Act - Delete one category
        categoryRepository.delete(savedDesserts);

        // Assert - Only one category should remain
        List<Category> allCategories = categoryRepository.findAll();
        assertThat(allCategories).hasSize(1);
        assertThat(allCategories.get(0).getName()).isEqualTo("Beverages");
    }

    /**
     * Test: Find categories ordered by display order
     */
    @Test
    @DisplayName("Should retrieve categories ordered by display order")
    void testFindAllByOrderByDisplayOrderAsc_ShouldReturnOrderedList() {
        // Arrange - Save categories in random order
        categoryRepository.save(new Category("Desserts", 3));
        categoryRepository.save(new Category("Main Course", 2));
        categoryRepository.save(new Category("Beverages", 1));

        // Act
        List<Category> orderedCategories = categoryRepository.findAllByOrderByDisplayOrderAsc();

        // Assert
        assertThat(orderedCategories).hasSize(3);
        assertThat(orderedCategories.get(0).getName()).isEqualTo("Beverages");
        assertThat(orderedCategories.get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(orderedCategories.get(1).getName()).isEqualTo("Main Course");
        assertThat(orderedCategories.get(1).getDisplayOrder()).isEqualTo(2);
        assertThat(orderedCategories.get(2).getName()).isEqualTo("Desserts");
        assertThat(orderedCategories.get(2).getDisplayOrder()).isEqualTo(3);
    }

    /**
     * Test Case 05 (TC05) - Integration: Negative display order persistence
     * Documents that negative displayOrder values are persisted (BUG)
     */
    @Test
    @DisplayName("TC05 Integration: Should persist negative display order (BUG TEST)")
    void testSaveCategory_WithNegativeDisplayOrder_IsPersisted() {
        // Arrange
        Category categoryWithNegativeOrder = new Category("Invalid Category", -1);

        // Act
        Category savedCategory = categoryRepository.save(categoryWithNegativeOrder);

        // Assert - This demonstrates the BUG
        Optional<Category> found = categoryRepository.findById(savedCategory.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getDisplayOrder()).isEqualTo(-1);

        // TODO: Once bug is fixed with database constraint or validation,
        // this test should verify that negative values are rejected at DB level
    }

    /**
     * Test: Find category by name
     */
    @Test
    @DisplayName("Should find category by name")
    void testFindByName_ShouldReturnCategory() {
        // Arrange
        categoryRepository.save(beveragesCategory);

        // Act
        Optional<Category> found = categoryRepository.findByName("Beverages");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Beverages");
        assertThat(found.get().getDisplayOrder()).isEqualTo(1);
    }

    /**
     * Test: Check if category exists by name
     */
    @Test
    @DisplayName("Should check if category exists by name")
    void testExistsByName_ShouldReturnTrue() {
        // Arrange
        categoryRepository.save(beveragesCategory);

        // Act
        boolean exists = categoryRepository.existsByName("Beverages");
        boolean notExists = categoryRepository.existsByName("NonExistent");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    /**
     * Test: Update category
     */
    @Test
    @DisplayName("Should update existing category")
    void testUpdateCategory_ShouldPersistChanges() {
        // Arrange
        Category savedCategory = categoryRepository.save(beveragesCategory);
        Long categoryId = savedCategory.getId();

        // Act - Update the category
        savedCategory.setName("Hot Beverages");
        savedCategory.setDisplayOrder(5);
        categoryRepository.save(savedCategory);

        // Assert
        Optional<Category> updatedCategory = categoryRepository.findById(categoryId);
        assertThat(updatedCategory).isPresent();
        assertThat(updatedCategory.get().getName()).isEqualTo("Hot Beverages");
        assertThat(updatedCategory.get().getDisplayOrder()).isEqualTo(5);
        assertThat(updatedCategory.get().getUpdatedAt()).isAfter(updatedCategory.get().getCreatedAt());
    }
}
