package com.farmacyfood.fridge.service;

import com.farmacyfood.fridge.dto.HeladeraCreateDTO;
import com.farmacyfood.fridge.dto.HeladeraResponseDTO;
import com.farmacyfood.fridge.dto.HeladeraUpdateDTO;

import java.util.List;

public interface HeladeraService {
    List<HeladeraResponseDTO> findAll(String status, Double lat, Double lng, Double radius);
    HeladeraResponseDTO findById(Long id);
    HeladeraResponseDTO create(HeladeraCreateDTO dto);
    HeladeraResponseDTO update(Long id, HeladeraUpdateDTO dto);
    void delete(Long id);
}
