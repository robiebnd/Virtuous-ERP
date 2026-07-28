package com.digipals.wms.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateWarehouseRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String address;

    private String city;

    private String country;
}