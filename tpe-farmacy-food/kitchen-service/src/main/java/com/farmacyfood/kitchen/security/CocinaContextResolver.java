package com.farmacyfood.kitchen.security;

import com.farmacyfood.kitchen.entity.postgres.Cocina;
import com.farmacyfood.kitchen.exception.CocinaException;
import com.farmacyfood.kitchen.repository.CocinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CocinaContextResolver {

    private final CocinaRepository cocinaRepository;

    public Cocina resolver() {
        String username = AuthContext.getCurrentUsername();
        return cocinaRepository.findByUsuario(username)
                .orElseThrow(() -> new CocinaException("El usuario " + username + " no tiene una cocina asignada"));
    }
}
