package com.digipals.wms.goodsmovement.dto;

import com.digipals.wms.goodsmovement.entity.GoodsMovementType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGoodsMovementRequest {

    @NotNull(message = "Movement type is required")
    private GoodsMovementType movementType;

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    @NotNull(message = "Reference number is required")
    private String referenceNumber;

    @NotNull(message = "Reference type is required")
    private String referenceType;

    private String remarks;

    @Valid
    @NotEmpty(
            message = "At least one movement line is required"
    )
    @Builder.Default
    private List<CreateGoodsMovementLineRequest> lines = new ArrayList<>();
}
