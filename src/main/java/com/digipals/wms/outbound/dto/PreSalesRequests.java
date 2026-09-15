package com.digipals.wms.outbound.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class PreSalesRequests {
    private PreSalesRequests() {}

    public record InquiryLineRequest(
            @NotBlank String sku,
            String description,
            @NotNull @Positive BigDecimal quantity,
            LocalDateTime requestedDeliveryDate) {}

    public record CreateInquiryRequest(
            @NotBlank String customerNumber,
            String salesAreaCode,
            LocalDate requestedValidUntil,
            @NotNull @Size(min=1) List<@Valid InquiryLineRequest> lines,
            String currency,
            String notes) {}

    public record QuotationLineRequest(
            @NotBlank String sku,
            String description,
            @NotNull @Positive BigDecimal quantity,
            @NotNull @PositiveOrZero BigDecimal unitPrice,
            @PositiveOrZero BigDecimal discountAmount,
            @PositiveOrZero BigDecimal taxAmount,
            LocalDateTime requestedDeliveryDate) {}

    public record CreateQuotationRequest(
            String inquiryNumber,
            @NotBlank String customerNumber,
            String salesAreaCode,
            @NotNull LocalDate validFrom,
            @NotNull LocalDate validTo,
            @NotNull @Size(min=1) List<@Valid QuotationLineRequest> lines,
            String currency,
            String notes) {}

    public record ConvertQuotationRequest(
            @NotBlank String warehouseCode,
            LocalDateTime requestedDeliveryDate,
            String paymentTerms) {}
}
