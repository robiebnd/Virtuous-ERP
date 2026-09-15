package com.digipals.wms.outbounddelivery.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOutboundDeliveryRequest(
        UUID salesOrderId,
        @NotBlank String shippingPoint,
        LocalDateTime requestedDeliveryDate
) {}
