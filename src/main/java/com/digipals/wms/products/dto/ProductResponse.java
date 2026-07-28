package com.digipals.wms.products.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProductResponse {

    private UUID id;

    private String sku;

    private String name;

    private String description;

    private BigDecimal costPrice;

    private BigDecimal sellingPrice;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String categoryCode;

    private String categoryName;

    private String unitCode;

    private String unitName;
}