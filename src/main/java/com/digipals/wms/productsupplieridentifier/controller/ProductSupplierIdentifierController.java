package com.digipals.wms.productsupplieridentifier.controller;

import com.digipals.wms.productsupplieridentifier.dto.CreateProductSupplierIdentifierRequest;
import com.digipals.wms.productsupplieridentifier.dto.ProductSupplierIdentifierResponse;
import com.digipals.wms.productsupplieridentifier.dto.UpdateProductSupplierIdentifierRequest;
import com.digipals.wms.productsupplieridentifier.service.ProductSupplierIdentifierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-supplier-identifiers")
@RequiredArgsConstructor
public class ProductSupplierIdentifierController {

    private final ProductSupplierIdentifierService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PRODUCT_SUPPLIER_IDENTIFIER_CREATE')")
    public ProductSupplierIdentifierResponse create(
            @Valid @RequestBody CreateProductSupplierIdentifierRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_SUPPLIER_IDENTIFIER_UPDATE')")
    public ProductSupplierIdentifierResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductSupplierIdentifierRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_SUPPLIER_IDENTIFIER_VIEW')")
    public List<ProductSupplierIdentifierResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_SUPPLIER_IDENTIFIER_VIEW')")
    public ProductSupplierIdentifierResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/supplier/{supplierId}/item/{supplierItemCode}")
    @PreAuthorize("hasAuthority('PRODUCT_SUPPLIER_IDENTIFIER_VIEW')")
    public ProductSupplierIdentifierResponse findBySupplierAndCode(
            @PathVariable UUID supplierId,
            @PathVariable String supplierItemCode) {
        return service.findBySupplierAndCode(supplierId, supplierItemCode);
    }

    @GetMapping("/supplier-code/{supplierCode}/item/{supplierItemCode}")
    @PreAuthorize("hasAuthority('PRODUCT_SUPPLIER_IDENTIFIER_VIEW')")
    public ProductSupplierIdentifierResponse findBySupplierCodeAndItemCode(
            @PathVariable String supplierCode,
            @PathVariable String supplierItemCode) {
        return service.findBySupplierCodeAndItemCode(supplierCode, supplierItemCode);
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_SUPPLIER_IDENTIFIER_VIEW')")
    public List<ProductSupplierIdentifierResponse> findByProduct(@PathVariable UUID productId) {
        return service.findByProduct(productId);
    }

    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAuthority('PRODUCT_SUPPLIER_IDENTIFIER_VIEW')")
    public List<ProductSupplierIdentifierResponse> findBySupplier(@PathVariable UUID supplierId) {
        return service.findBySupplier(supplierId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('PRODUCT_SUPPLIER_IDENTIFIER_DELETE')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
