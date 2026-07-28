package com.digipals.wms.common.mapper;

import com.digipals.wms.uom.dto.UnitOfMeasureResponse;
import com.digipals.wms.uom.entity.UnitOfMeasure;

public final class UnitOfMeasureMapper {

    private UnitOfMeasureMapper() {
    }

    public static UnitOfMeasureResponse toResponse(
            UnitOfMeasure unit) {

        if (unit == null) {
            return null;
        }

        return UnitOfMeasureResponse.builder()

                .id(unit.getId())

                .code(unit.getCode())

                .name(unit.getName())

                .description(unit.getDescription())

                .active(unit.getActive())

                .createdAt(unit.getCreatedAt())

                .updatedAt(unit.getUpdatedAt())

                .build();
    }
}