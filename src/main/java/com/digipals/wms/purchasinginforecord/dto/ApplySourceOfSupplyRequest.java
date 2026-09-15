package com.digipals.wms.purchasinginforecord.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ApplySourceOfSupplyRequest {

    @NotNull
    private UUID purchasingInfoRecordId;
}
