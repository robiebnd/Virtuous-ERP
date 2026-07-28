package com.digipals.wms.users.controller;

import com.digipals.wms.users.dto.CreateUserRequest;
import com.digipals.wms.users.dto.UpdateUserRequest;
import com.digipals.wms.users.dto.UserResponse;
import com.digipals.wms.users.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public UserResponse create(
            @RequestBody CreateUserRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<UserResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}