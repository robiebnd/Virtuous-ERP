package com.digipals.wms.security;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.users.entity.UserRole;
import com.digipals.wms.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrentUserServiceImpl
        implements CurrentUserService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {

            throw new ResourceNotFoundException(
                    "No authenticated user found.");
        }

        String username =
                authentication.getName();

        return userRepository.findByUsername(username)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found."));
    }

    @Override
    public UUID getCurrentUserId() {

        return getCurrentUser().getId();
    }

    @Override
    public String getUsername() {

        return getCurrentUser().getUsername();
    }

    @Override
    public boolean hasRole(String role) {

        User user = getCurrentUser();

        return user.getUserRoles()

                .stream()

                .map(UserRole::getRole)

                .anyMatch(r ->
                        r.getName().equalsIgnoreCase(role));
    }

    @Override
    public boolean hasAuthority(String authority) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getAuthorities()

                .stream()

                .map(GrantedAuthority::getAuthority)

                .anyMatch(a ->
                        a.equalsIgnoreCase(authority));
    }
}