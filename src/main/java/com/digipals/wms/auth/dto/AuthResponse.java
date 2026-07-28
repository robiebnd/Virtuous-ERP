package com.digipals.wms.auth.dto;

import lombok.AllArgsConstructor;
import com.digipals.wms.users.dto.UserResponse;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

     private UserResponse user;
}