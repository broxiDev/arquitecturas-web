package com.farmacyfood.fridge.repository;

import com.farmacyfood.fridge.entity.EventoEstadoHeladera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusEventRepository extends JpaRepository<EventoEstadoHeladera, Long> {

    List<EventoEstadoHeladera> findByHeladeraIdOrderByTimestampDesc(Long heladeraId);
}
