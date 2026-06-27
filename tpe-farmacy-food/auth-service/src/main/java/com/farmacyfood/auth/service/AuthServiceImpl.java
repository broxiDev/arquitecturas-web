package com.farmacyfood.auth.service;

import com.farmacyfood.auth.config.JwtUtil;
import com.farmacyfood.auth.dto.AuthResponse;
import com.farmacyfood.auth.dto.LoginRequest;
import com.farmacyfood.auth.dto.RegisterRequest;
import com.farmacyfood.auth.entity.AuthUser;
import com.farmacyfood.auth.exception.DuplicateUserException;
import com.farmacyfood.auth.exception.InvalidCredentialsException;
import com.farmacyfood.auth.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Set<String> ALLOWED_ROLES = Set.of("cliente", "cocina");

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (!ALLOWED_ROLES.contains(request.rol())) {
            throw new IllegalArgumentException("Rol no permitido: '" + request.rol()
                    + "'. Roles permitidos: cliente, cocina");
        }

        if (authUserRepository.existsByUsername(request.username())) {
            throw new DuplicateUserException("El usuario '" + request.username() + "' ya existe");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        AuthUser authUser = new AuthUser(request.username(), hashedPassword, request.rol());
        authUserRepository.save(authUser);

        String token = jwtUtil.generateToken(authUser.getUsername(), authUser.getRol());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        AuthUser authUser = authUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), authUser.getPassword())) {
            throw new InvalidCredentialsException("Usuario o contraseña incorrectos");
        }

        String token = jwtUtil.generateToken(authUser.getUsername(), authUser.getRol());
        return new AuthResponse(token);
    }
}
