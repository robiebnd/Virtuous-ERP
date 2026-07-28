package com.digipals.wms.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSupplierRequest {

    @NotBlank(message = "Supplier name is required")
    private String name;

    private String contactPerson;

    @Email(message = "Invalid email address")
    private String email;

    private String phone;

    private String address;

    private String city;

    private String country;

    private Boolean active;
}