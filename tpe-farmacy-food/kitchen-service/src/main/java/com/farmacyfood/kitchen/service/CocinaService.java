package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.CocinaCreateDTO;
import com.farmacyfood.kitchen.dto.CocinaResponseDTO;

public interface CocinaService {

    CocinaResponseDTO crear(CocinaCreateDTO request);

    CocinaResponseDTO buscar(Long cocinaId);
}
