package com.digipals.wms.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CreateBillingRequest(
        UUID outboundDeliveryId,
        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "[A-Za-z]{3}", message = "Currency must be a 3-letter ISO code")
        String currency
) {
}
