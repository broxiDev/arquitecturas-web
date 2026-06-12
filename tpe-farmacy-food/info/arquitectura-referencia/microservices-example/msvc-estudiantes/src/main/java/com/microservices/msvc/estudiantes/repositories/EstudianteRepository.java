package com.microservices.msvc.estudiantes.repositories;

import com.microservices.msvc.estudiantes.entities.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findAllByOrderByApellidoAsc();

    Optional<Estudiante> findByLu(Long lu);

    List<Estudiante> findByGeneroOrderByApellidoAsc(String genero);

    // Usado por msvc-carreras via Feign para el query g) carrera+ciudad
    List<Estudiante> findByDniIn(List<Long> dnis);
}
