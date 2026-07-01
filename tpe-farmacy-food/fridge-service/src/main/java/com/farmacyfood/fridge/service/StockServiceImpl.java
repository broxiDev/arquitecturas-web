package com.farmacyfood.fridge.service;

import com.farmacyfood.audit.client.AuditLogger;
import com.farmacyfood.fridge.client.DisponibilidadNotificacionDTO;
import com.farmacyfood.fridge.client.NotificacionClient;
import com.farmacyfood.fridge.constants.AuditMessages;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    @Autowired
    private AuditLogger auditLogger;

    private final StockRepository stockRepository;
    private final HeladeraRepository heladeraRepository;
    private final NotificacionClient notificacionClient;

    @Override
    @Transactional(readOnly = true)
    public List<FridgeRemainderDTO> getRemainderByCocinaId(Long cocinaId) {
        List<StockHeladera> stockItems = stockRepository.findByCocinaId(cocinaId);

        Map<Long, List<StockHeladera>> grouped = stockItems.stream()
            .collect(Collectors.groupingBy(s -> s.getHeladera().getId()));

        List<FridgeRemainderDTO> result = new ArrayList<>();
        for (Map.Entry<Long, List<StockHeladera>> entry : grouped.entrySet()) {
            List<ProductRemainderDTO> products = entry.getValue().stream()
                .map(s -> new ProductRemainderDTO(s.getProductId(), s.getProductName(), s.getQuantity()))
                .toList();
            result.add(new FridgeRemainderDTO(entry.getKey(), products));
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
        try {
            Heladera heladera = heladeraRepository.findById(heladeraId)
                .orElseThrow(() -> new HeladeraNotFoundException("No existe heladera con id: " + heladeraId));

            StockHeladera stock = StockHeladera.builder()
                .heladera(heladera)
                .cocinaId(dto.cocinaId())
                .productId(dto.productId())
                .productName(dto.productName())
                .quantity(dto.quantity())
                .price(dto.price())
                .build();

            StockHeladera saved = stockRepository.save(stock);

            if ("ACTIVE".equals(heladera.getStatus())) {
                notificacionClient.notificarProductoDisponible(
                    new DisponibilidadNotificacionDTO(heladeraId, List.of(dto.productId())));
            }

            StockResponseDTO response = toDTO(saved);
            auditLogger.success("ADD_STOCK", AuditMessages.STOCK_ADDED, response);
            return response;
        } catch (HeladeraNotFoundException e) {
            auditLogger.error("ADD_STOCK", AuditMessages.FRIDGE_NOT_FOUND + ": " + heladeraId, heladeraId);
            throw e;
        } catch (Exception e) {
            auditLogger.error("ADD_STOCK", "Error al agregar stock: " + e.getMessage(), dto);
            throw e;
        }
    }

    @Override
    @Transactional
    public StockResponseDTO updateStock(Long heladeraId, StockUpdateDTO dto) {
        try {
            StockHeladera stock = stockRepository.findByHeladeraIdAndCocinaIdAndProductId(heladeraId, dto.cocinaId(), dto.productId())
                .orElseThrow(() -> new HeladeraNotFoundException(
                    "No existe stock para el producto " + dto.productId() + " (cocina " + dto.cocinaId() + ") en la heladera " + heladeraId));

            int oldQuantity = stock.getQuantity();
            stock.setQuantity(dto.quantity());
            if (dto.productName() != null) stock.setProductName(dto.productName());
            if (dto.price() != null) stock.setPrice(dto.price());
            StockHeladera saved = stockRepository.save(stock);

            if (oldQuantity == 0 && dto.quantity() > 0) {
                notificacionClient.notificarProductoDisponible(
                    new DisponibilidadNotificacionDTO(heladeraId, List.of(dto.productId())));
            }

            StockResponseDTO response = toDTO(saved);
            auditLogger.success("UPDATE_STOCK", AuditMessages.STOCK_UPDATED, response);
            return response;
        } catch (HeladeraNotFoundException e) {
            auditLogger.error("UPDATE_STOCK", AuditMessages.STOCK_NOT_FOUND + ": " + dto.productId(), dto);
            throw e;
        } catch (Exception e) {
            auditLogger.error("UPDATE_STOCK", "Error al actualizar stock: " + e.getMessage(), dto);
            throw e;
        }
    }

    private StockResponseDTO toDTO(StockHeladera stock) {
        return new StockResponseDTO(
            stock.getId(),
            stock.getHeladera().getId(),
            stock.getCocinaId(),
            stock.getProductId(),
            stock.getProductName(),
            stock.getQuantity(),
            stock.getPrice(),
            stock.getUpdatedAt()
        );
    }
}
