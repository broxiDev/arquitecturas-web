package com.farmacyfood.auth.service;

import com.farmacyfood.audit.client.AuditLogger;
import com.farmacyfood.auth.client.UserClient;
import com.farmacyfood.auth.config.JwtUtil;
import com.farmacyfood.auth.constants.AuditMessages;
import com.farmacyfood.auth.dto.AuthResponse;
import com.farmacyfood.auth.dto.LoginRequest;
import com.farmacyfood.auth.dto.RegisterRequest;
import com.farmacyfood.auth.dto.UserRegistrationRequest;
import com.farmacyfood.auth.entity.AuthUser;
import com.farmacyfood.auth.exception.DuplicateUserException;
import com.farmacyfood.auth.exception.InvalidCredentialsException;
import com.farmacyfood.auth.repository.AuthUserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Set<String> ALLOWED_ROLES = Set.of("cliente", "cocina");
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditLogger auditLogger;
    private final UserClient userClient;

    @Override
    public AuthResponse register(RegisterRequest request) {
        try {
            if (!ALLOWED_ROLES.contains(request.rol())) {
                auditLogger.error("REGISTER", AuditMessages.INVALID_ROLE, "rol: " + request.rol());
                throw new IllegalArgumentException("Rol no permitido: '" + request.rol()
                        + "'. Roles permitidos: cliente, cocina");
            }

            if (authUserRepository.existsByUsername(request.username())) {
                auditLogger.error("REGISTER", AuditMessages.USER_ALREADY_EXISTS, "username: " + request.username());
                throw new DuplicateUserException("El usuario '" + request.username() + "' ya existe");
            }

            String hashedPassword = passwordEncoder.encode(request.password());
            AuthUser authUser = new AuthUser(request.username(), hashedPassword, request.rol());
            authUserRepository.save(authUser);

            // 3. Llamar a user-service para crear perfil
            try {
                userClient.register(new UserRegistrationRequest(
                        request.name(),
                        request.email(),
                        request.username(),  // authUsername = mismo username
                        List.of()             // dietaryPreferences vacío por defecto
                ));
            } catch (FeignException e) {
                // ¿rollback de auth?
                authUserRepository.delete(authUser);
                throw new RuntimeException("Error al crear perfil en user-service", e);
            }

            String token = jwtUtil.generateToken(authUser.getUsername(), authUser.getRol());
            auditLogger.success("REGISTER", AuditMessages.USER_REGISTERED, "username: " + request.username());
            return new AuthResponse(token);
        } catch (Exception e) {
            log.warn("Error en registro: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            AuthUser authUser = authUserRepository.findByUsername(request.username())
                    .orElseThrow(() -> {
                        auditLogger.error("LOGIN", AuditMessages.LOGIN_FAILED, "username: " + request.username());
                        return new InvalidCredentialsException("Usuario o contraseña incorrectos");
                    });

            if (!passwordEncoder.matches(request.password(), authUser.getPassword())) {
                auditLogger.error("LOGIN", AuditMessages.LOGIN_FAILED, "username: " + request.username());
                throw new InvalidCredentialsException("Usuario o contraseña incorrectos");
            }

            String token = jwtUtil.generateToken(authUser.getUsername(), authUser.getRol());
            auditLogger.success("LOGIN", AuditMessages.USER_LOGGED_IN, "username: " + request.username());
            return new AuthResponse(token);
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            auditLogger.error("LOGIN", "Error inesperado en inicio de sesión", e.getMessage());
            throw e;
        }
    }
}
