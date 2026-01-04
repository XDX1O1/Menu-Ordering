package menuorderingapp.project.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import menuorderingapp.project.model.dto.CategoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Category Validation Tests")
class CategoryValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Test Case 02 (TC02): Add category with empty name and display order
     * Expected Result: Validation fails and category cannot be created
     * Actual Result: As Expected
     */
    @Test
    @DisplayName("TC02: Should fail validation when name is empty")
    void testCreateCategory_WithEmptyName() {
        // Arrange - Empty name
        CategoryRequest request = new CategoryRequest("", 1);

        // Act
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name") &&
                v.getMessage().contains("Category name is required"));
    }

    /**
     * Test Case 02 (TC02): Add category with null name
     */
    @Test
    @DisplayName("TC02: Should fail validation when name is null")
    void testCreateCategory_WithNullName() {
        // Arrange - Null name
        CategoryRequest request = new CategoryRequest(null, 1);

        // Act
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name") &&
                v.getMessage().contains("Category name is required"));
    }

    /**
     * Test Case 04 (TC04): Add category with whitespace-only name
     * Expected Result: System shows error message and category cannot be created
     * Actual Result: As Expected
     */
    @Test
    @DisplayName("TC04: Should fail validation when name contains only whitespace")
    void testCreateCategory_WithWhitespaceOnlyName() {
        // Arrange - Whitespace-only name
        CategoryRequest request = new CategoryRequest("   ", 1);

        // Act
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name") &&
                v.getMessage().contains("Category name is required"));
    }

    /**
     * Test Case 04 (TC04): Add category with whitespace in displayOrder field
     * Note: displayOrder is Integer type, so whitespace would cause type conversion
     * error
     * This test verifies the request model handles null displayOrder properly
     */
    @Test
    @DisplayName("TC04: Should allow null displayOrder (optional field)")
    void testCreateCategory_WithNullDisplayOrder() {
        // Arrange - Valid name with null displayOrder
        CategoryRequest request = new CategoryRequest("Beverages", null);

        // Act
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);

        // Assert - displayOrder is optional, so no violations expected
        assertThat(violations).isEmpty();
    }

    /**
     * Test Case: Valid category request should pass validation
     */
    @Test
    @DisplayName("Should pass validation with valid name and display order")
    void testCreateCategory_WithValidData() {
        // Arrange - Valid data
        CategoryRequest request = new CategoryRequest("Beverages", 1);

        // Act
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }
}
