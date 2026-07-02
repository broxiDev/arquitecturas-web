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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final StockRepository stockRepository;
    private final HeladeraRepository heladeraRepository;
    private final NotificacionClient notificacionClient;

    @Override
    @Transactional(readOnly = true)
    public List<FridgeRemainderDTO> getRemainderByUsername(String username) {
        List<StockHeladera> stockItems = stockRepository.findByUsername(username);

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
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            Heladera heladera = heladeraRepository.findById(heladeraId)
                .orElseThrow(() -> new HeladeraNotFoundException("No existe heladera con id: " + heladeraId));

            StockHeladera stock = StockHeladera.builder()
                .heladera(heladera)
                .username(username)
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

            return toDTO(saved);
        } catch (HeladeraNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public StockResponseDTO updateStock(Long heladeraId, StockUpdateDTO dto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            StockHeladera stock = stockRepository.findByHeladeraIdAndUsernameAndProductId(heladeraId, username, dto.productId())
                .orElseThrow(() -> new HeladeraNotFoundException(
                    "No existe stock para el producto " + dto.productId() + " en la heladera " + heladeraId));

            int oldQuantity = stock.getQuantity();
            stock.setQuantity(dto.quantity());
            if (dto.productName() != null) stock.setProductName(dto.productName());
            if (dto.price() != null) stock.setPrice(dto.price());
            StockHeladera saved = stockRepository.save(stock);

            if (oldQuantity == 0 && dto.quantity() > 0) {
                notificacionClient.notificarProductoDisponible(
                    new DisponibilidadNotificacionDTO(heladeraId, List.of(dto.productId())));
            }

            return toDTO(saved);
        } catch (HeladeraNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    private StockResponseDTO toDTO(StockHeladera stock) {
        return new StockResponseDTO(
            stock.getId(),
            stock.getHeladera().getId(),
            stock.getUsername(),
            stock.getProductId(),
            stock.getProductName(),
            stock.getQuantity(),
            stock.getPrice(),
            stock.getUpdatedAt()
        );
    }
}
