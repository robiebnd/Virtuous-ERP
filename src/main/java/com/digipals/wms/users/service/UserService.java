package com.digipals.wms.users.service;

import com.digipals.wms.users.dto.CreateUserRequest;
import com.digipals.wms.users.dto.UpdateUserRequest;
import com.digipals.wms.users.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse create(
            CreateUserRequest request);

    UserResponse update(
            UUID id,
            UpdateUserRequest request);

    UserResponse findById(
            UUID id);

    List<UserResponse> findAll();

    void delete(
            UUID id);
}
