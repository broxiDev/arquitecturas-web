package com.farmacyfood.fridge.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class NotificacionClientMockImpl implements NotificacionClient {

    @Override
    public void notificarProductoDisponible(DisponibilidadNotificacionDTO notificacion) {
        log.info("[MOCK] Notificando productos disponibles - Heladera: {}, Productos: {}",
            notificacion.fridgeId(), notificacion.productIds());
    }

    @Override
    public void notificarHeladeraStatusChange(HeladeraStatusChangeDTO alerta) {
        log.info("[MOCK] Alerta de cambio de estado - Heladera {} ({}): {} -> {}",
            alerta.heladeraId(), alerta.heladeraName(), alerta.oldStatus(), alerta.newStatus());
    }
}
