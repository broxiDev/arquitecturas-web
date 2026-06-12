package com.microservices.msvc.carreras.repositories;

import com.microservices.msvc.carreras.entities.EstudianteCarrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudianteCarreraRepository extends JpaRepository<EstudianteCarrera, Long> {

    List<EstudianteCarrera> findByCarrera_NombreCarrera(String nombreCarrera);
}
