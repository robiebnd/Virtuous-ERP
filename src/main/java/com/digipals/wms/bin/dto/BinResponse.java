package com.digipals.wms.bin.dto;

import com.digipals.wms.bin.entity.BinType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BinResponse {

    private UUID id;

    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private String code;

    private String name;

    private BinType type;

    private BigDecimal capacity;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
