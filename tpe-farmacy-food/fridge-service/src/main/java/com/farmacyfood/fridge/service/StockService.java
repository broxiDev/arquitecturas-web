package com.farmacyfood.fridge.service;

import com.farmacyfood.fridge.dto.FridgeRemainderDTO;
import com.farmacyfood.fridge.dto.StockCreateDTO;
import com.farmacyfood.fridge.dto.StockResponseDTO;
import com.farmacyfood.fridge.dto.StockUpdateDTO;

import java.util.List;

public interface StockService {
    List<StockResponseDTO> getStockByHeladera(Long heladeraId);
    StockResponseDTO addStock(Long heladeraId, StockCreateDTO dto);
    StockResponseDTO updateStock(Long heladeraId, StockUpdateDTO dto);
    List<FridgeRemainderDTO> getRemainderByCocinaId(Long cocinaId);
}
