package com.digipals.wms.common.mapper;

import com.digipals.wms.products.category.dto.ProductCategoryResponse;
import com.digipals.wms.products.category.entity.ProductCategory;



public final class ProductCategoryMapper {

    private ProductCategoryMapper() {
    }

    public static ProductCategoryResponse toResponse(
            ProductCategory category) {

        if (category == null) {
            return null;
        }

        return ProductCategoryResponse.builder()

                .id(category.getId())

                .code(category.getCode())

                .name(category.getName())

                .description(category.getDescription())

                .active(category.getActive())

                .createdAt(category.getCreatedAt())

                .updatedAt(category.getUpdatedAt())

                .build();
    }
}