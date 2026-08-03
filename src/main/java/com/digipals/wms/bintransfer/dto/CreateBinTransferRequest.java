package com.digipals.wms.bintransfer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBinTransferRequest {

    @NotNull
    private UUID warehouseId;

    @NotNull
    private UUID fromBinId;

    @NotNull
    private UUID toBinId;

    private String remarks;
}
