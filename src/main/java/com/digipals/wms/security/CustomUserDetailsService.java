package com.digipals.wms.security;

import com.digipals.wms.roles.entity.Permission;
import com.digipals.wms.roles.entity.RolePermission;
import com.digipals.wms.roles.repository.RolePermissionRepository;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.users.entity.UserRole;
import com.digipals.wms.users.repository.UserRepository;
import com.digipals.wms.users.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final RolePermissionRepository rolePermissionRepository;

    @Override
public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new UsernameNotFoundException(
                            "User '" + username + "' not found."));

    Set<GrantedAuthority> authorities = new HashSet<>();

    for (UserRole userRole :
            userRoleRepository.findByUserId(user.getId())) {

        // Role authority
        authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + userRole.getRole().getName()));

        // Permission authorities
        for (RolePermission rolePermission :
                rolePermissionRepository.findByRole(userRole.getRole())) {

            Permission permission =
                    rolePermission.getPermission();

            authorities.add(
                    new SimpleGrantedAuthority(
                            permission.getCode()));
        }
    }

    return org.springframework.security.core.userdetails.User
            .builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .disabled(!Boolean.TRUE.equals(user.getEnabled()))
            .accountLocked(Boolean.TRUE.equals(user.getAccountLocked()))
            .authorities(authorities)
            .build();
}
}