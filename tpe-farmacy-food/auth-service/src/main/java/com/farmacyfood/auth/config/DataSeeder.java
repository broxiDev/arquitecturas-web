package com.farmacyfood.auth.config;

import com.farmacyfood.auth.entity.AuthUser;
import com.farmacyfood.auth.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String username = "admin-heladera";

        if (!authUserRepository.existsByUsername(username)) {
            String hashedPassword = passwordEncoder.encode("admin123");
            AuthUser admin = new AuthUser(username, hashedPassword, "adminDeHeladera");
            authUserRepository.save(admin);
            log.info("Seed data: usuario '{}' creado con rol 'adminDeHeladera'", username);
        } else {
            log.info("Seed data: usuario '{}' ya existe, omitiendo", username);
        }
    }
}
