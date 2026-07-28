package com.digipals.wms.users.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UpdateUserRequest {

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private Boolean enabled;

    private Boolean accountLocked;

    private UUID defaultWarehouseId;

    private Set<UUID> roleIds;
}