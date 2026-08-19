package com.digipals.wms.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateProductRequest {

    /**
     * Optional ERP product number.
     * When omitted, the ERP assigns an internal non-mnemonic SKU such as SKU-482731.
     * When supplied, it is treated as an externally assigned ERP product number.
     */
    private String sku;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Cost price is required")
    @PositiveOrZero(message = "Cost price cannot be negative")
    private BigDecimal costPrice;

    @NotNull(message = "Selling price is required")
    @PositiveOrZero(message = "Selling price cannot be negative")
    private BigDecimal sellingPrice;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotNull(message = "Unit of Measure is required")
    private UUID unitOfMeasureId;

    private Boolean active = true;
}
