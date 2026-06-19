package com.farmacyfood.fridge.client;

import java.util.List;

public record DisponibilidadNotificacionDTO(
    Long fridgeId,
    List<Long> productIds
) {}
