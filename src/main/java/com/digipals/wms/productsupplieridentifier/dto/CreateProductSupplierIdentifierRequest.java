package com.digipals.wms.productsupplieridentifier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateProductSupplierIdentifierRequest {

    @NotNull(message = "Product is required")
    private UUID productId;

    @NotNull(message = "Supplier is required")
    private UUID supplierId;

    @NotBlank(message = "Supplier item code is required")
    private String supplierItemCode;

    private String supplierItemName;
}
