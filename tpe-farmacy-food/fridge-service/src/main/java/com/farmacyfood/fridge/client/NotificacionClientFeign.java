package com.farmacyfood.fridge.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${clients.notification-service.url:http://localhost:8087}")
@Profile("!dev")
public interface NotificacionClientFeign extends NotificacionClient {

    @Override
    @PostMapping("/api/v1/notificaciones/producto-disponible")
    void notificarProductoDisponible(@RequestBody DisponibilidadNotificacionDTO notificacion);
}
