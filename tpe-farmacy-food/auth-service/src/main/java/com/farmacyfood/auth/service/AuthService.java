package com.farmacyfood.auth.service;

import com.farmacyfood.auth.dto.AuthResponse;
import com.farmacyfood.auth.dto.LoginRequest;
import com.farmacyfood.auth.entity.AuthUser;

public interface AuthService {
    AuthUser register(LoginRequest request);
    AuthResponse login(LoginRequest request);
}
