package com.digipals.wms.roles.service;

import com.digipals.wms.roles.dto.CreateRoleRequest;
import com.digipals.wms.roles.dto.RoleResponse;
import com.digipals.wms.roles.dto.UpdateRoleRequest;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleResponse create(
            CreateRoleRequest request);

    RoleResponse update(
            UUID id,
            UpdateRoleRequest request);

    RoleResponse findById(
            UUID id);

    List<RoleResponse> findAll();

    void delete(
            UUID id);
}