package com.digipals.wms.bin.dto;

import com.digipals.wms.bin.entity.BinStatus;
import com.digipals.wms.bin.entity.BinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBinRequest {

    @NotBlank
    private String name;

    @NotNull
    private BinType type;

    private BigDecimal capacity;

    private Boolean active;

    private BinStatus status;

    private String barcode;

    private Integer sequence;

    private String description;
}