package com.digipals.wms.outbound.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class O2cRequests {
    private O2cRequests() {}
    public record CustomerRequest(String customerNumber, String name, String email, String phone,
                                  String billingAddress, String shippingAddress, String paymentTerms,
                                  BigDecimal creditLimit) {}
    public record OrderLineRequest(String sku, BigDecimal quantity, BigDecimal unitPrice) {}
    public record SalesOrderRequest(String customerNumber, String warehouseCode, LocalDateTime requestedDeliveryDate,
                                    String paymentTerms, List<OrderLineRequest> lines) {}
    public record DeliveryRequest(String orderNumber) {}
    public record PickRequest(String deliveryNumber) {}
    public record InvoiceRequest(String deliveryNumber) {}
    public record PaymentRequest(String invoiceNumber, BigDecimal amount, String paymentMethod, String reference) {}
}
