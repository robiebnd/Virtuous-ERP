package com.digipals.wms.common.mapper;

import com.digipals.wms.products.Product;
import com.digipals.wms.products.dto.ProductResponse;


public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductResponse toResponse(Product product) {

        if (product == null) {
            return null;
        }

        return ProductResponse.builder()

                .id(product.getId())

                .sku(product.getSku())

                .name(product.getName())

                .description(product.getDescription())

                .costPrice(product.getCostPrice())

                .sellingPrice(product.getSellingPrice())

                .active(product.getActive())

                .createdAt(product.getCreatedAt())

                .updatedAt(product.getUpdatedAt())

                .categoryCode(
                        product.getCategory() == null
                                ? null
                                : product.getCategory().getCode())

                .categoryName(
                        product.getCategory() == null
                                ? null
                                : product.getCategory().getName())

                .unitCode(
                        product.getUnitOfMeasure() == null
                                ? null
                                : product.getUnitOfMeasure().getCode())

                .unitName(
                        product.getUnitOfMeasure() == null
                                ? null
                                : product.getUnitOfMeasure().getName())

                .build();
    }
}