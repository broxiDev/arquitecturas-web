package com.farmacyfood.fridge.repository;

import com.farmacyfood.fridge.entity.EventoEstadoHeladera;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusEventRepository extends MongoRepository<EventoEstadoHeladera, String> {

    List<EventoEstadoHeladera> findByHeladeraIdOrderByTimestampDesc(Long heladeraId);
}
