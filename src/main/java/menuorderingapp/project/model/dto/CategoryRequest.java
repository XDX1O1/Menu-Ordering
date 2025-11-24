package menuorderingapp.project.model.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private Integer displayOrder;


    public CategoryRequest() {
    }

    public CategoryRequest(String name, Integer displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
