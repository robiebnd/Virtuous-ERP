package com.digipals.wms.bin.dto;

import com.digipals.wms.bin.entity.BinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBinRequest {

    @NotNull
    private UUID warehouseId;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    private BinType type;

    private BigDecimal capacity;
}
