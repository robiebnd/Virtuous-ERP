package com.digipals.wms.dunning.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDunningRequest(
        @NotNull UUID billingDocumentId,
        Integer dunningLevel,
        String message
) {
}
