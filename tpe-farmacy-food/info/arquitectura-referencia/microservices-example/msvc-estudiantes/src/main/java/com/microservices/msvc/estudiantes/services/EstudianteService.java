package com.microservices.msvc.estudiantes.services;

import com.microservices.msvc.estudiantes.entities.Estudiante;

import java.util.List;
import java.util.Optional;

public interface EstudianteService {
    List<Estudiante> findAll() throws Exception;
    Optional<Estudiante> findById(Long dni) throws Exception;
    Optional<Estudiante> findByLu(Long lu) throws Exception;
    List<Estudiante> findByGenero(String genero) throws Exception;
    List<Estudiante> findBulk(List<Long> dnis) throws Exception;
    Estudiante save(Estudiante entity) throws Exception;
}
