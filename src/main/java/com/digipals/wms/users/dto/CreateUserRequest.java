package com.digipals.wms.users.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateUserRequest {

    private String username;

    private String password;

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private UUID defaultWarehouseId;

    private Set<UUID> roleIds;
}