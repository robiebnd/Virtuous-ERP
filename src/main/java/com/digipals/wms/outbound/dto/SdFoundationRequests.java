package com.digipals.wms.outbound.dto;

public final class SdFoundationRequests {
    private SdFoundationRequests() {}
    public record OrganizationRequest(String code, String name, String currency, String country) {}
    public record CodeNameRequest(String code, String name) {}
    public record SalesAreaRequest(String code, String salesOrganizationCode, String distributionChannelCode, String divisionCode) {}
    public record ShippingPointRequest(String code, String name, String warehouseCode, String address) {}
    public record SalesOfficeRequest(String code, String name, String salesOrganizationCode) {}
    public record SalesGroupRequest(String code, String name, String salesOfficeCode) {}
    public record CustomerSalesAreaRequest(String customerNumber, String salesAreaCode, String salesOfficeCode, String salesGroupCode, String paymentTerms, String customerPricingGroup, Integer deliveryPriority, String shippingCondition, java.math.BigDecimal creditLimit) {}
    public record ProductSalesDataRequest(String sku, String salesAreaCode, String salesUnit, String taxClassification, String itemCategoryGroup, String deliveringWarehouseCode, String shippingPointCode, java.math.BigDecimal minimumOrderQuantity) {}
    public record CustomerMaterialRequest(String customerNumber, String sku, String customerMaterialNumber, String customerDescription, String customerUnit) {}
    public record PricingConditionRequest(String conditionType, String salesAreaCode, String customerNumber, String sku, java.time.LocalDateTime validFrom, java.time.LocalDateTime validTo, java.math.BigDecimal rate, String rateType, String currency, Integer priority) {}
}
