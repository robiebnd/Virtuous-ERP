package com.digipals.wms.common.mapper;

import com.digipals.wms.supplier.dto.SupplierResponse;
import com.digipals.wms.supplier.entity.Supplier;

public final class SupplierMapper {

    private SupplierMapper() {
    }

    public static SupplierResponse toResponse(
            Supplier supplier) {

        if (supplier == null) {
            return null;
        }

        return SupplierResponse.builder()

                .id(supplier.getId())

                .code(supplier.getCode())

                .name(supplier.getName())

                .contactPerson(supplier.getContactPerson())

                .email(supplier.getEmail())

                .phone(supplier.getPhone())

                .address(supplier.getAddress())

                .city(supplier.getCity())

                .country(supplier.getCountry())

                .active(supplier.getActive())

                .createdAt(supplier.getCreatedAt())

                .updatedAt(supplier.getUpdatedAt())

                .build();
    }
}