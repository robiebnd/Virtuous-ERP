package com.digipals.wms.products;

import com.digipals.wms.products.dto.CreateProductRequest;
import com.digipals.wms.products.dto.ProductResponse;
import com.digipals.wms.products.dto.UpdateProductRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(
            UUID id,
            UpdateProductRequest request);

    ProductResponse findById(UUID id);

    ProductResponse findBySku(String sku);

    List<ProductResponse> findAll();

    List<ProductResponse> findActive();

    List<ProductResponse> findByCategory(UUID categoryId);

    void delete(UUID id);
}