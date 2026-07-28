package com.digipals.wms.supplier.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SupplierResponse {

    private UUID id;

    private String code;

    private String name;

    private String contactPerson;

    private String email;

    private String phone;

    private String address;

    private String city;

    private String country;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}