package com.digipals.wms.roles.service;

import com.digipals.wms.common.mapper.PermissionMapper;
import com.digipals.wms.roles.dto.CreatePermissionRequest;
import com.digipals.wms.roles.dto.PermissionResponse;
import com.digipals.wms.roles.dto.UpdatePermissionRequest;
import com.digipals.wms.roles.entity.Permission;
import com.digipals.wms.roles.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl
        implements PermissionService {

    private final PermissionRepository repository;

    @Override
    public PermissionResponse create(
            CreatePermissionRequest request) {

        if (repository.existsByCode(request.getCode())) {

            throw new RuntimeException(
                    "Permission already exists.");
        }

        Permission permission =
                Permission.builder()
                        .code(request.getCode())
                        .description(request.getDescription())
                        .build();

        permission = repository.save(permission);

        return PermissionMapper.toResponse(permission);
    }

    @Override
    public PermissionResponse update(
            UUID id,
            UpdatePermissionRequest request) {

        Permission permission =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Permission not found."));

        if (!permission.getCode().equals(request.getCode())
                && repository.existsByCode(request.getCode())) {

            throw new RuntimeException(
                    "Permission already exists.");
        }

        permission.setCode(request.getCode());

        permission.setDescription(request.getDescription());

        permission = repository.save(permission);

        return PermissionMapper.toResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse findById(
            UUID id) {

        Permission permission =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Permission not found."));

        return PermissionMapper.toResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(PermissionMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(
            UUID id) {

        Permission permission =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Permission not found."));

        repository.delete(permission);
    }
}