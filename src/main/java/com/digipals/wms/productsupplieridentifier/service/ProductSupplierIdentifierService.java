package com.digipals.wms.productsupplieridentifier.service;

import com.digipals.wms.productsupplieridentifier.dto.CreateProductSupplierIdentifierRequest;
import com.digipals.wms.productsupplieridentifier.dto.ProductSupplierIdentifierResponse;
import com.digipals.wms.productsupplieridentifier.dto.UpdateProductSupplierIdentifierRequest;

import java.util.List;
import java.util.UUID;

public interface ProductSupplierIdentifierService {

    ProductSupplierIdentifierResponse create(CreateProductSupplierIdentifierRequest request);

    ProductSupplierIdentifierResponse update(UUID id, UpdateProductSupplierIdentifierRequest request);

    ProductSupplierIdentifierResponse findById(UUID id);

    ProductSupplierIdentifierResponse findBySupplierAndCode(UUID supplierId, String supplierItemCode);

    ProductSupplierIdentifierResponse findBySupplierCodeAndItemCode(String supplierCode, String supplierItemCode);

    List<ProductSupplierIdentifierResponse> findByProduct(UUID productId);

    List<ProductSupplierIdentifierResponse> findBySupplier(UUID supplierId);

    List<ProductSupplierIdentifierResponse> findAll();

    void delete(UUID id);
}
