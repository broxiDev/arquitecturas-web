package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.FridgeRemainderDTO;

import java.util.List;

// Interfaz del cliente para comunicarse con fridge-service
public interface FridgeClient {
    // Obtiene el remanente de productos en heladeras asociadas a una cocina
    List<FridgeRemainderDTO> getRemainderByKitchen(String cocinaId);
}