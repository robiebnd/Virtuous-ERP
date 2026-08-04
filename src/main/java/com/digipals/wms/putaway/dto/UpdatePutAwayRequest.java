package com.digipals.wms.putaway.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePutAwayRequest {

    @NotNull
    private UUID warehouseId;

    private UUID assignedTo;

    private String remarks;
}