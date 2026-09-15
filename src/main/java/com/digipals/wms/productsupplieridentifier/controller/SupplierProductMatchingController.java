package com.digipals.wms.productsupplieridentifier.controller;

import com.digipals.wms.productsupplieridentifier.dto.SupplierProductLookupResponse;
import com.digipals.wms.productsupplieridentifier.service.SupplierProductMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product-supplier-identifiers/lookup")
@RequiredArgsConstructor
public class SupplierProductMatchingController {

    private final SupplierProductMatchingService service;

    @GetMapping("supplier-code/{supplierCode}/item/{supplierItemCode}")
    public SupplierProductLookupResponse resolve(
            @PathVariable String supplierCode,
            @PathVariable String supplierItemCode) {
        return service.resolve(supplierCode, supplierItemCode);
    }
}
