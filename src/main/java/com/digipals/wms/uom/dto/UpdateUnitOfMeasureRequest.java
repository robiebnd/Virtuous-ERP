package com.digipals.wms.uom.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUnitOfMeasureRequest {

    @NotBlank(message = "Unit name is required")
    private String name;

    private String description;

    private Boolean active;
}