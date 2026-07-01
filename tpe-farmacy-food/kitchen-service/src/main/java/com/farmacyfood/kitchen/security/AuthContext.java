package com.farmacyfood.kitchen.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthContext {

    private AuthContext() {
    }

    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static String getCurrentRole() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
    }
}
