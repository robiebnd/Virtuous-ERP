package com.digipals.wms.common.mapper;

import com.digipals.wms.warehouse.dto.WarehouseResponse;
import com.digipals.wms.warehouse.entity.Warehouse;

public class WarehouseMapper {

    private WarehouseMapper() {
    }

    public static WarehouseResponse toResponse(
            Warehouse warehouse) {

        return WarehouseResponse.builder()

                .id(warehouse.getId())

                .code(warehouse.getCode())

                .name(warehouse.getName())

                .address(warehouse.getAddress())

                .city(warehouse.getCity())

                .country(warehouse.getCountry())

                .active(warehouse.getActive())

                .createdAt(
                        warehouse.getCreatedAt())

                .build();
    }
}