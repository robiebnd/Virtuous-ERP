package com.digipals.wms.salesorder.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateSalesOrderRequest(
        @NotBlank String customerCode,
        @NotBlank String salesOrganization,
        @NotBlank String distributionChannel,
        @NotBlank String division,
        String remarks,
        @NotEmpty List<@Valid CreateSalesOrderItemRequest> items
) {
}
