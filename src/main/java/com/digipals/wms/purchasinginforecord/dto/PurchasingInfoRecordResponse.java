package com.digipals.wms.purchasinginforecord.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class PurchasingInfoRecordResponse {
    UUID id;
    UUID supplierProductIdentifierId;
    UUID productId;
    String sku;
    String productName;
    UUID supplierId;
    String supplierCode;
    String supplierName;
    String supplierItemCode;
    String supplierItemName;
    UUID warehouseId;
    String warehouseCode;
    String warehouseName;
    String currency;
    BigDecimal lastPurchasePrice;
    BigDecimal standardOrderQuantity;
    Integer plannedDeliveryDays;
    LocalDate validFrom;
    LocalDate validTo;
    Boolean regularSupplier;
    Boolean automaticSourcing;
    Boolean active;
}
