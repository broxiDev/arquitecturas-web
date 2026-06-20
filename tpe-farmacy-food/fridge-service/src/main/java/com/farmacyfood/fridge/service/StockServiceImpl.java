package com.farmacyfood.fridge.service;

import com.farmacyfood.fridge.client.DisponibilidadNotificacionDTO;
import com.farmacyfood.fridge.client.NotificacionClient;
import com.farmacyfood.fridge.dto.FridgeRemainderDTO;
import com.farmacyfood.fridge.dto.ProductRemainderDTO;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final HeladeraRepository heladeraRepository;
    private final NotificacionClient notificacionClient;

    @Override
    @Transactional(readOnly = true)
    public List<FridgeRemainderDTO> getRemainderByCocinaId(String cocinaId) {
        List<Heladera> heladeras = heladeraRepository.findByCocinaId(cocinaId);
        List<FridgeRemainderDTO> result = new ArrayList<>();

        for (Heladera heladera : heladeras) {
            List<StockHeladera> stockItems = stockRepository.findByHeladeraId(heladera.getId());
            List<ProductRemainderDTO> products = new ArrayList<>();

            for (StockHeladera stock : stockItems) {
                products.add(new ProductRemainderDTO(
                    stock.getProductId(),
                    stock.getProductName(),
                    stock.getQuantity()
                ));
            }

            result.add(new FridgeRemainderDTO(heladera.getId(), products));
        }

        return result;
    }

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
            .productName(dto.productName())
            .quantity(dto.quantity())
            .build();

        StockHeladera saved = stockRepository.save(stock);

        if ("ACTIVE".equals(heladera.getStatus())) {
            notificacionClient.notificarProductoDisponible(
                new DisponibilidadNotificacionDTO(heladeraId, List.of(dto.productId())));
        }

        return toDTO(saved);
    }

    @Override
    @Transactional
    public StockResponseDTO updateStock(Long heladeraId, StockUpdateDTO dto) {
        StockHeladera stock = stockRepository.findByHeladeraIdAndProductId(heladeraId, dto.productId())
            .orElseThrow(() -> new HeladeraNotFoundException(
                "No existe stock para el producto " + dto.productId() + " en la heladera " + heladeraId));

        int oldQuantity = stock.getQuantity();
        stock.setQuantity(dto.quantity());
        if (dto.productName() != null) stock.setProductName(dto.productName());
        StockHeladera saved = stockRepository.save(stock);

        if (oldQuantity == 0 && dto.quantity() > 0) {
            notificacionClient.notificarProductoDisponible(
                new DisponibilidadNotificacionDTO(heladeraId, List.of(dto.productId())));
        }

        return toDTO(saved);
    }

    private StockResponseDTO toDTO(StockHeladera stock) {
        return new StockResponseDTO(
            stock.getId(),
            stock.getHeladera().getId(),
            stock.getProductId(),
            stock.getProductName(),
            stock.getQuantity(),
            stock.getUpdatedAt()
        );
    }
}
