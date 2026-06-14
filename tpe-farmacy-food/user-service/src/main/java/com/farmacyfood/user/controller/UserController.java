package com.farmacyfood.user.controller;

import com.farmacyfood.user.dto.UserRegistrationRequest;
import com.farmacyfood.user.dto.UserResponse;
import com.farmacyfood.user.entity.User;
import com.farmacyfood.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "User Service", description = "Perfiles, preferencias e historial de usuarios")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar un nuevo usuario")
    @PostMapping("/registrar")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        User user = new User(request.name(), request.email(), request.passwordHash(), request.dietaryPreferences());
        User saved = service.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(saved));
    }

    @Operation(summary = "Obtener perfil de usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(user -> ResponseEntity.ok(UserResponse.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar preferencias dietarias del usuario")
    @PutMapping("/{id}/preferencias")
    public ResponseEntity<UserResponse> updatePreferences(
            @PathVariable Long id,
            @RequestBody List<String> dietaryPreferences) {
        User updated = service.updatePreferences(id, dietaryPreferences);
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    @Operation(summary = "Obtener historial de compras del usuario (consulta a order-service)")
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<?>> getPurchaseHistory(@PathVariable Long id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.getPurchaseHistory(id));
    }

    @Operation(summary = "Health check del servicio")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "user-service"));
    }
}
