package com.digipals.wms.users.service;

import com.digipals.wms.common.mapper.UserMapper;
import com.digipals.wms.roles.entity.Role;
import com.digipals.wms.roles.repository.RoleRepository;
import com.digipals.wms.users.dto.CreateUserRequest;
import com.digipals.wms.users.dto.UpdateUserRequest;
import com.digipals.wms.users.dto.UserResponse;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.users.entity.UserRole;
import com.digipals.wms.users.repository.UserRepository;
import com.digipals.wms.users.repository.UserRoleRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl
        implements UserService {

    private final UserRepository repository;

    private final WarehouseRepository warehouseRepository;

    private final RoleRepository roleRepository;

    private final UserRoleRepository userRoleRepository;

    @Override
    public UserResponse create(
            CreateUserRequest request) {

        if (repository.existsByUsername(
                request.getUsername())) {

            throw new RuntimeException(
                    "Username already exists.");
        }

        if (repository.existsByEmail(
                request.getEmail())) {

            throw new RuntimeException(
                    "Email already exists.");
        }

        User user =
                User.builder()
                        .username(
                                request.getUsername())
                        .password(
                                request.getPassword()) // PasswordEncoder later
                        .email(
                                request.getEmail())
                        .firstName(
                                request.getFirstName())
                        .lastName(
                                request.getLastName())
                        .phoneNumber(
                                request.getPhoneNumber())
                        .enabled(true)
                        .accountLocked(false)
                        .build();

        if (request.getDefaultWarehouseId() != null) {

            Warehouse warehouse =
                    warehouseRepository.findById(
                            request.getDefaultWarehouseId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Warehouse not found"));

            user.setDefaultWarehouse(
                    warehouse);
        }

        user = repository.save(
                user);

        if (request.getRoleIds() != null &&
                !request.getRoleIds().isEmpty()) {

            for (UUID roleId : request.getRoleIds()) {

                Role role =
                        roleRepository.findById(roleId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Role not found."));

                UserRole assignment =
                        UserRole.builder()
                                .user(user)
                                .role(role)
                                .build();

                userRoleRepository.save(
                        assignment);
            }
        }

        Set<String> roles =
                userRoleRepository.findByUserId(
                                user.getId())
                        .stream()
                        .map(UserRole::getRole)
                        .map(Role::getName)
                        .collect(Collectors.toSet());

        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(user -> {

                    Set<String> roles =
                            userRoleRepository.findByUserId(
                                            user.getId())
                                    .stream()
                                    .map(UserRole::getRole)
                                    .map(Role::getName)
                                    .collect(Collectors.toSet());

                    return UserMapper.toResponse(user);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(
            UUID id) {

        User user =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        Set<String> roles =
                userRoleRepository.findByUserId(
                                user.getId())
                        .stream()
                        .map(UserRole::getRole)
                        .map(Role::getName)
                        .collect(Collectors.toSet());

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse update(
            UUID id,
            UpdateUserRequest request) {

        throw new UnsupportedOperationException(
                "Update will be implemented next.");
    }

    @Override
    public void delete(
            UUID id) {

        userRoleRepository.deleteByUserId(
                id);

        repository.deleteById(
                id);
    }
}