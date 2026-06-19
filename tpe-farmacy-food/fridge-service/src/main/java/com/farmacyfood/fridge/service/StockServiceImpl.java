package com.farmacyfood.fridge.service;

import com.farmacyfood.fridge.dto.StockCreateDTO;
import com.farmacyfood.fridge.dto.StockResponseDTO;
import com.farmacyfood.fridge.dto.StockUpdateDTO;
import com.farmacyfood.fridge.entity.Heladera;
import com.farmacyfood.fridge.entity.StockHeladera;
import com.farmacyfood.fridge.exception.HeladeraNotFoundException;
import com.farmacyfood.fridge.repository.HeladeraRepository;
import com.farmacyfood.fridge.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final HeladeraRepository heladeraRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockResponseDTO> getStockByHeladera(Long heladeraId) {
        return stockRepository.findByHeladeraId(heladeraId).stream()
            .map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public StockResponseDTO addStock(Long heladeraId, StockCreateDTO dto) {
        Heladera heladera = heladeraRepository.findById(heladeraId)
            .orElseThrow(() -> new HeladeraNotFoundException("No existe heladera con id: " + heladeraId));

        StockHeladera stock = StockHeladera.builder()
            .heladera(heladera)
            .productId(dto.productId())
            .quantity(dto.quantity())
            .build();

        StockHeladera saved = stockRepository.save(stock);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public StockResponseDTO updateStock(Long heladeraId, StockUpdateDTO dto) {
        StockHeladera stock = stockRepository.findByHeladeraIdAndProductId(heladeraId, dto.productId())
            .orElseThrow(() -> new HeladeraNotFoundException(
                "No existe stock para el producto " + dto.productId() + " en la heladera " + heladeraId));

        stock.setQuantity(dto.quantity());
        StockHeladera saved = stockRepository.save(stock);
        return toDTO(saved);
    }

    private StockResponseDTO toDTO(StockHeladera stock) {
        return new StockResponseDTO(
            stock.getId(),
            stock.getHeladera().getId(),
            stock.getProductId(),
            stock.getQuantity(),
            stock.getUpdatedAt()
        );
    }
}
