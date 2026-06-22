package com.farmacyfood.fridge.client;

public record HeladeraStatusChangeDTO(
    Long heladeraId,
    String heladeraName,
    String newStatus,
    String oldStatus
) {}
