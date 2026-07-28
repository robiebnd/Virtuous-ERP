package com.digipals.wms.products.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProductCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private Boolean active;
}
