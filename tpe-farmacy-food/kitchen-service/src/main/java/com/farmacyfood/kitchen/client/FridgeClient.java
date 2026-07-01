package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.CargaProductoDTO;
import com.farmacyfood.kitchen.dto.FridgeRemainderDTO;

import java.util.List;

// Interfaz del cliente para comunicarse con fridge-service
public interface FridgeClient {
    // Obtiene el remanente de productos en heladeras asociadas al usuario autenticado
    List<FridgeRemainderDTO> getRemainderByKitchen();

    // Carga productos en una heladera especifica
    void cargarStockEnHeladera(Long heladeraId, List<CargaProductoDTO> productos);
}
