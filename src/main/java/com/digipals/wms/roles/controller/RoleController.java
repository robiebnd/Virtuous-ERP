package com.digipals.wms.roles.controller;

import com.digipals.wms.roles.dto.CreateRoleRequest;
import com.digipals.wms.roles.dto.RoleResponse;
import com.digipals.wms.roles.dto.UpdateRoleRequest;
import com.digipals.wms.roles.service.RoleService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public RoleResponse create(
            @RequestBody CreateRoleRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<RoleResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public RoleResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @PutMapping("/{id}")
    public RoleResponse update(
            @PathVariable UUID id,
            @RequestBody UpdateRoleRequest request) {

        return service.update(
                id,
                request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}