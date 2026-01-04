package menuorderingapp.project.service;

import menuorderingapp.project.model.Category;
import menuorderingapp.project.repository.CategoryRepository;
import menuorderingapp.project.service.impl.MenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Service Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MenuAuditService menuAuditService;

    @Mock
    private menuorderingapp.project.repository.MenuRepository menuRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    private Category validCategory;
    private Category categoryWithNegativeDisplayOrder;

    @BeforeEach
    void setUp() {
        validCategory = new Category("Beverages", 1);
        validCategory.setId(1L);

        categoryWithNegativeDisplayOrder = new Category("Desserts", -1);
    }

    @Test
    @DisplayName("TC01: Should successfully save category with valid data")
    void testSaveCategory_WithValidData() {
        when(categoryRepository.save(any(Category.class))).thenReturn(validCategory);

        Category savedCategory = menuService.saveCategory(validCategory);

        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Beverages");
        assertThat(savedCategory.getDisplayOrder()).isEqualTo(1);
        verify(categoryRepository, times(1)).save(validCategory);
    }

    @Test
    @DisplayName("TC03: Should successfully delete existing category")
    void testDeleteCategory_WhenExists() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(validCategory));
        doNothing().when(categoryRepository).delete(validCategory);

        menuService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(validCategory);
    }

    @Test
    @DisplayName("TC03: Should throw exception when deleting non-existent category")
    void testDeleteCategory_WhenNotExists() {
        Long nonExistentId = 999L;
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.deleteCategory(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found with id: " + nonExistentId);

        verify(categoryRepository, times(1)).findById(nonExistentId);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    @DisplayName("Urutan Tampilan Negatif(-1)")
    void testSaveCategory_WithNegativeDisplayOrder() {
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryWithNegativeDisplayOrder);

        Category savedCategory = menuService.saveCategory(categoryWithNegativeDisplayOrder);

        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getDisplayOrder()).isEqualTo(-1);

        verify(categoryRepository, times(1)).save(categoryWithNegativeDisplayOrder);
    }
}
