package com.digipals.wms.users.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private Boolean enabled;

    private Boolean accountLocked;

    /*
     * Default Warehouse
     */
    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    /*
     * Security
     */
    private Set<String> roles;

    private Set<String> permissions;

    /*
     * Audit
     */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



   
}