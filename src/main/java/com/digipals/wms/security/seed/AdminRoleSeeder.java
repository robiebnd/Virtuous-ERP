package com.digipals.wms.security.seed;

import com.digipals.wms.roles.entity.Permission;
import com.digipals.wms.roles.entity.Role;
import com.digipals.wms.roles.entity.RolePermission;
import com.digipals.wms.roles.repository.PermissionRepository;
import com.digipals.wms.roles.repository.RolePermissionRepository;
import com.digipals.wms.roles.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminRoleSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

       Role administrator = roleRepository
        .findByName("SYSTEM_ADMIN")
        .orElseThrow(() ->
                new RuntimeException("SYSTEM_ADMIN role not found."));
        for (Permission permission : permissionRepository.findAll()) {
            
            // Check if the link already exists to avoid unique constraint violations
            boolean exists = rolePermissionRepository
                    .existsByRoleAndPermission(administrator, permission);

            if (!exists) {
                rolePermissionRepository.save(
                        RolePermission.builder()
                                .role(administrator)
                                .permission(permission)
                                .build());
            }
        }

        System.out.println("Administrator permissions seeded successfully.");
    }
}