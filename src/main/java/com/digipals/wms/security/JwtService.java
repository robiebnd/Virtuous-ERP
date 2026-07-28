package com.digipals.wms.security;

import com.digipals.wms.roles.entity.Permission;
import com.digipals.wms.roles.entity.Role;
import com.digipals.wms.roles.entity.RolePermission;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.users.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final String SECRET =
            "this-is-a-very-long-secret-key-for-wms-system-2026-super-secret-key";

    private static final long EXPIRATION =
            1000L * 60 * 60 * 24;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate JWT from authenticated user.
     */
    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();

        Set<String> roles = user.getUserRoles()

                .stream()

                .map(UserRole::getRole)

                .map(Role::getName)

                .collect(Collectors.toSet());

        Set<String> permissions = user.getUserRoles()

                .stream()

                .map(UserRole::getRole)

                .flatMap(role ->
                        role.getRolePermissions().stream())

                .map(RolePermission::getPermission)

                .map(Permission::getCode)

                .collect(Collectors.toSet());

        claims.put("roles", roles);

        claims.put("permissions", permissions);

        if (user.getDefaultWarehouse() != null) {

            claims.put(
                    "warehouseId",
                    user.getDefaultWarehouse().getId());

            claims.put(
                    "warehouseCode",
                    user.getDefaultWarehouse().getCode());

            claims.put(
                    "warehouseName",
                    user.getDefaultWarehouse().getName());
        }

        return generateToken(
                claims,
                user.getUsername());
    }

    /**
     * Legacy overload.
     */
    public String generateToken(
            String username) {

        return generateToken(
                new HashMap<>(),
                username);
    }

    /**
     * Internal JWT builder.
     */
    public String generateToken(
            Map<String, Object> claims,
            String username) {

        return Jwts.builder()

                .claims(claims)

                .subject(username)

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION))

                .signWith(getSigningKey())

                .compact();
    }

    /**
     * Username
     */
    public String extractUsername(
            String token) {

        return extractClaims(token)
                .getSubject();
    }

    /**
     * All Claims
     */
    public Claims extractAllClaims(
            String token) {

        return extractClaims(token);
    }

    /**
     * Roles
     */
    public Set<String> extractRoles(
            String token) {

        return new HashSet<>(

                extractClaims(token)

                        .get("roles", java.util.List.class));
    }

    /**
     * Permissions
     */
    public Set<String> extractPermissions(
            String token) {

        return new HashSet<>(

                extractClaims(token)

                        .get("permissions", java.util.List.class));
    }

    /**
     * Warehouse Id
     */
    public UUID extractWarehouseId(
            String token) {

        Object value =
                extractClaims(token)
                        .get("warehouseId");

        if (value == null) {
            return null;
        }

        return UUID.fromString(
                value.toString());
    }

    /**
     * Warehouse Code
     */
    public String extractWarehouseCode(
            String token) {

        return extractClaims(token)

                .get(
                        "warehouseCode",
                        String.class);
    }

    /**
     * Warehouse Name
     */
    public String extractWarehouseName(
            String token) {

        return extractClaims(token)

                .get(
                        "warehouseName",
                        String.class);
    }

    /**
     * Validate Token
     */
    public boolean isValid(
            String token,
            UserDetails userDetails) {

        return extractUsername(token)

                .equals(
                        userDetails.getUsername())

                && extractClaims(token)

                .getExpiration()

                .after(new Date());
    }

    /**
     * Internal Claims Parser
     */
    private Claims extractClaims(
            String token) {

        return Jwts.parser()

                .verifyWith(getSigningKey())

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }
}