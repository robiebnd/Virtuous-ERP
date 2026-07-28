package com.digipals.wms.auth.service;

import com.digipals.wms.auth.dto.AuthResponse;
import com.digipals.wms.auth.dto.LoginRequest;
import com.digipals.wms.auth.dto.RegisterRequest;
import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.UserMapper;
import com.digipals.wms.roles.entity.Role;
import com.digipals.wms.roles.repository.RoleRepository;
import com.digipals.wms.security.JwtService;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.users.entity.UserRole;
import com.digipals.wms.users.repository.UserRepository;
import com.digipals.wms.users.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
    private static final String DEFAULT_ROLE = "WAREHOUSE_CLERK";

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username already exists.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already exists.");
        }

        // First user becomes SYSTEM_ADMIN.
        // All subsequent users receive the default role.
        String roleName = userRepository.count() == 0
                ? SYSTEM_ADMIN
                : DEFAULT_ROLE;

        Role role = getRole(roleName);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .accountLocked(false)
                .defaultWarehouse(null) // assigned later
                .build();

        user = userRepository.save(user);

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRoleRepository.save(userRole);
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        User user = userRepository.findByUsername(
                        request.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."));

        // JwtService should generate claims from the User object.
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
        .token(token)
        .user(UserMapper.toResponse(user))
        .build();
    }

    private Role getRole(String roleName) {

        return roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role '" + roleName + "' does not exist."));
    }
}