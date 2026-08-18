package com.digipals.wms.putaway.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePutAwayFromGoodsReceiptNumberRequest {

    @NotNull(message = "Staging bin is required")
    private UUID fromBinId;

    private String remarks;
}
