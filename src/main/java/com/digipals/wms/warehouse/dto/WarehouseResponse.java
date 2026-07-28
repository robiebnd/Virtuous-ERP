package com.digipals.wms.warehouse.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WarehouseResponse {

    private UUID id;

    private String code;

    private String name;

    private String address;

    private String city;

    private String country;

    private Boolean active;

    private LocalDateTime createdAt;
}
