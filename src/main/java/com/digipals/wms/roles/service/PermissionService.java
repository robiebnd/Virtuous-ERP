package com.digipals.wms.roles.service;

import com.digipals.wms.roles.dto.CreatePermissionRequest;
import com.digipals.wms.roles.dto.PermissionResponse;
import com.digipals.wms.roles.dto.UpdatePermissionRequest;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    PermissionResponse create(
            CreatePermissionRequest request);

    PermissionResponse update(
            UUID id,
            UpdatePermissionRequest request);

    PermissionResponse findById(
            UUID id);

    List<PermissionResponse> findAll();

    void delete(
            UUID id);
}