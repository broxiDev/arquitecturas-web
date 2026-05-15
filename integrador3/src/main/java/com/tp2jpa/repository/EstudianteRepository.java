package com.tp2jpa.repository;

import com.tp2jpa.entities.Estudiante;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad `Estudiante`.
 *
 * Define consultas específicas utilizadas por el servicio de estudiantes,
 * incluyendo búsqueda por LU, género y combinación carrera/ciudad.
 */
@Repository("EstudianteRepository")
public interface EstudianteRepository extends RepoBase<Estudiante, Long> {

    List<Estudiante> findAllByOrderByApellidoAsc();

    Optional<Estudiante> findByLu(Long lu);

    List<Estudiante> findByGeneroOrderByApellidoAsc(String genero);

    @Query("SELECT e FROM Estudiante e JOIN e.inscripciones i JOIN i.carrera c " +
           "WHERE c.nombreCarrera = :carrera AND e.ciudad = :ciudad ORDER BY e.apellido ASC")
    List<Estudiante> findByCarreraAndCiudad(@Param("carrera") String carrera, @Param("ciudad") String ciudad);
}
