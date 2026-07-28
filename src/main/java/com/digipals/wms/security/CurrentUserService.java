package com.digipals.wms.security;

import com.digipals.wms.users.entity.User;

import java.util.UUID;

public interface CurrentUserService {

    User getCurrentUser();

    UUID getCurrentUserId();

    String getUsername();

    boolean hasRole(String role);

    boolean hasAuthority(String authority);
}