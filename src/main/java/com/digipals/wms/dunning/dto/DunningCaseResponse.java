package com.digipals.wms.dunning.dto;

import com.digipals.wms.dunning.entity.DunningStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DunningCaseResponse(
        UUID id,
        String dunningNumber,
        UUID billingDocumentId,
        String customerCode,
        String currency,
        BigDecimal outstandingAmount,
        LocalDateTime dueDate,
        LocalDateTime dunningDate,
        Integer dunningLevel,
        DunningStatus status,
        String message
) {
}
