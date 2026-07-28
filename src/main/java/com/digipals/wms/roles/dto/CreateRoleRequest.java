package com.digipals.wms.roles.dto;

import lombok.Data;

@Data
public class CreateRoleRequest {

    private String name;

    private String description;
}