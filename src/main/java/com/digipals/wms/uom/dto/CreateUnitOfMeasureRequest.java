package com.digipals.wms.uom.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUnitOfMeasureRequest {

    @NotBlank(message = "Unit code is required")
    private String code;

    @NotBlank(message = "Unit name is required")
    private String name;

    private String description;

    private Boolean active = true;
}