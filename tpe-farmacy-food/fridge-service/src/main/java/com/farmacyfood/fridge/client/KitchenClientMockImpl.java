package com.farmacyfood.fridge.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@Profile("dev")
public class KitchenClientMockImpl implements KitchenClient {

    private static final Set<Long> VALID_COCINAS = Set.of(1L, 2L, 3L);

    @Override
    public boolean cocinaExists(Long cocinaId) {
        boolean exists = VALID_COCINAS.contains(cocinaId);
        log.info("[MOCK] Verificando cocina {}: {}", cocinaId, exists ? "EXISTE" : "NO EXISTE");
        return exists;
    }
}
