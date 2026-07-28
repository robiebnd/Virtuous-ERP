package com.digipals.wms.roles.service;

import com.digipals.wms.common.mapper.RoleMapper;
import com.digipals.wms.roles.dto.CreateRoleRequest;
import com.digipals.wms.roles.dto.RoleResponse;
import com.digipals.wms.roles.dto.UpdateRoleRequest;
import com.digipals.wms.roles.entity.Role;
import com.digipals.wms.roles.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl
        implements RoleService {

    private final RoleRepository repository;

    @Override
    public RoleResponse create(
            CreateRoleRequest request) {

        if (repository.existsByName(
                request.getName())) {

            throw new RuntimeException(
                    "Role already exists.");
        }

        Role role =
                Role.builder()
                        .name(
                                request.getName())
                        .description(
                                request.getDescription())
                        .build();

        role =
                repository.save(
                        role);

        return RoleMapper.toResponse(
                role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse findById(
            UUID id) {

        Role role =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Role not found."));

        return RoleMapper.toResponse(
                role);
    }

    @Override
    public RoleResponse update(
            UUID id,
            UpdateRoleRequest request) {

        Role role =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Role not found."));

        if (!role.getName().equals(
                request.getName())
                && repository.existsByName(
                        request.getName())) {

            throw new RuntimeException(
                    "Role already exists.");
        }

        role.setName(
                request.getName());

        role.setDescription(
                request.getDescription());

        role =
                repository.save(
                        role);

        return RoleMapper.toResponse(
                role);
    }

    @Override
    public void delete(
            UUID id) {

        Role role =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Role not found."));

        repository.delete(
                role);
    }
}
