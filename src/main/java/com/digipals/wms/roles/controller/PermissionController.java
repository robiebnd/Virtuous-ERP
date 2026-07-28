package com.digipals.wms.roles.controller;

import com.digipals.wms.roles.dto.CreatePermissionRequest;
import com.digipals.wms.roles.dto.PermissionResponse;
import com.digipals.wms.roles.dto.UpdatePermissionRequest;
import com.digipals.wms.roles.service.PermissionService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService service;

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public PermissionResponse create(
            @RequestBody CreatePermissionRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<PermissionResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public PermissionResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @PutMapping("/{id}")
    public PermissionResponse update(
            @PathVariable UUID id,
            @RequestBody UpdatePermissionRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}