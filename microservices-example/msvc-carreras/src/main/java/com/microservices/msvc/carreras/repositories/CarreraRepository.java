package com.microservices.msvc.carreras.repositories;

import com.microservices.msvc.carreras.dto.CarreraInscriptosDTO;
import com.microservices.msvc.carreras.dto.CarreraReporteDTO;
import com.microservices.msvc.carreras.entities.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    @Query("SELECT new com.microservices.msvc.carreras.dto.CarreraInscriptosDTO(c.idCarrera, c.nombreCarrera, COUNT(i)) " +
           "FROM Carrera c JOIN c.inscripciones i " +
           "GROUP BY c.idCarrera, c.nombreCarrera " +
           "ORDER BY COUNT(i) DESC")
    List<CarreraInscriptosDTO> getCarrerasConInscriptos();

    @Query("SELECT new com.microservices.msvc.carreras.dto.CarreraReporteDTO(c.nombreCarrera, ec.inscripcion, COUNT(ec), 0L) " +
           "FROM EstudianteCarrera ec JOIN ec.carrera c " +
           "WHERE ec.inscripcion > 0 " +
           "GROUP BY c.nombreCarrera, ec.inscripcion " +
           "ORDER BY c.nombreCarrera ASC, ec.inscripcion ASC")
    List<CarreraReporteDTO> getInscriptosPorAnio();

    @Query("SELECT new com.microservices.msvc.carreras.dto.CarreraReporteDTO(c.nombreCarrera, ec.graduacion, 0L, COUNT(ec)) " +
           "FROM EstudianteCarrera ec JOIN ec.carrera c " +
           "WHERE ec.graduacion > 0 " +
           "GROUP BY c.nombreCarrera, ec.graduacion " +
           "ORDER BY c.nombreCarrera ASC, ec.graduacion ASC")
    List<CarreraReporteDTO> getEgresadosPorAnio();
}
