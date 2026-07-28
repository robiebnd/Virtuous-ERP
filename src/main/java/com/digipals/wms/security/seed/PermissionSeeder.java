package com.digipals.wms.security.seed;

import com.digipals.wms.roles.entity.Permission;
import com.digipals.wms.roles.repository.PermissionRepository;
import com.digipals.wms.security.Permissions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
@RequiredArgsConstructor
public class PermissionSeeder {

    private final PermissionRepository permissionRepository;

    @PostConstruct
    public void seedPermissions() {

        Field[] fields = Permissions.class.getDeclaredFields();

        for (Field field : fields) {

            try {

                String permissionCode = (String) field.get(null);

                if (!permissionRepository.existsByCode(permissionCode)) {

                    Permission permission =
                            Permission.builder()
                                    .code(permissionCode)
                                    .name(permissionCode.replace("_", " "))
                                    .description(permissionCode.replace("_", " "))
                                    .build();

                    permissionRepository.save(permission);
                }

            } catch (IllegalAccessException ignored) {
            }
        }
    }
}