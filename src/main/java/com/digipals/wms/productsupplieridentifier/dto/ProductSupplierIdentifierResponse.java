package com.digipals.wms.productsupplieridentifier.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ProductSupplierIdentifierResponse {

    UUID id;
    UUID productId;
    String sku;
    String productName;
    UUID supplierId;
    String supplierCode;
    String supplierName;
    String supplierItemCode;
    String supplierItemName;
    Boolean active;
}
