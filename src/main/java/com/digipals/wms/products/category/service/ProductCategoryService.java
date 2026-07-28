package com.digipals.wms.products.category.service;

import com.digipals.wms.products.category.dto.CreateProductCategoryRequest;
import com.digipals.wms.products.category.dto.ProductCategoryResponse;
import com.digipals.wms.products.category.dto.UpdateProductCategoryRequest;

import java.util.List;
import java.util.UUID;

public interface ProductCategoryService {

    ProductCategoryResponse create(
            CreateProductCategoryRequest request);

    ProductCategoryResponse update(
            UUID id,
            UpdateProductCategoryRequest request);

    ProductCategoryResponse findById(
            UUID id);

    ProductCategoryResponse findByCode(
            String code);

    List<ProductCategoryResponse> findAll();

    List<ProductCategoryResponse> findActive();

    void delete(
            UUID id);
}