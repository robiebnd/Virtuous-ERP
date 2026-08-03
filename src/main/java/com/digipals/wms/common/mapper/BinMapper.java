package com.digipals.wms.common.mapper;

import com.digipals.wms.bin.dto.BinResponse;
import com.digipals.wms.bin.entity.Bin;

public class BinMapper {

    public static BinResponse toResponse(Bin bin) {

        if (bin == null) {
            return null;
        }

        return BinResponse.builder()
                .id(bin.getId())
                .warehouseId(bin.getWarehouse().getId())
                .warehouseCode(bin.getWarehouse().getCode())
                .warehouseName(bin.getWarehouse().getName())
                .code(bin.getCode())
                .name(bin.getName())
                .type(bin.getType())
                .capacity(bin.getCapacity())
                .active(bin.getActive())
                .createdAt(bin.getCreatedAt())
                .updatedAt(bin.getUpdatedAt())
                .build();
    }
}
