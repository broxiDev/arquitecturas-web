package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.CatalogoLocalRequestDTO;
import com.farmacyfood.kitchen.dto.CatalogoLocalResponseDTO;

import java.util.List;

public interface CatalogoLocalService {

    CatalogoLocalResponseDTO registrar(CatalogoLocalRequestDTO request);

    List<CatalogoLocalResponseDTO> listarDeMiCocina();
}
