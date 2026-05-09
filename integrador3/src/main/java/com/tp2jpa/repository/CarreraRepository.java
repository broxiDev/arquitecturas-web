package com.tp2jpa.repository;

import com.tp2jpa.dto.CarreraInscriptosDTO;
import com.tp2jpa.dto.CarreraReporteDTO;
import com.tp2jpa.entities.Carrera;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CarreraRepository")
public interface CarreraRepository extends RepoBase<Carrera, Long> {

    @Query("SELECT new com.tp2jpa.dto.CarreraInscriptosDTO(c.nombreCarrera, COUNT(i)) " +
           "FROM Carrera c JOIN c.inscripciones i " +
           "GROUP BY c.idCarrera, c.nombreCarrera " +
           "ORDER BY COUNT(i) DESC")
    List<CarreraInscriptosDTO> getCarrerasConInscriptos();

    @Query("SELECT new com.tp2jpa.dto.CarreraReporteDTO(c.nombreCarrera, ec.inscripcion, COUNT(ec), 0L) " +
           "FROM EstudianteCarrera ec JOIN ec.carrera c " +
           "WHERE ec.inscripcion > 0 " +
           "GROUP BY c.nombreCarrera, ec.inscripcion " +
           "ORDER BY c.nombreCarrera ASC, ec.inscripcion ASC")
    List<CarreraReporteDTO> getInscriptosPorAnio();

    @Query("SELECT new com.tp2jpa.dto.CarreraReporteDTO(c.nombreCarrera, ec.graduacion, 0L, COUNT(ec)) " +
           "FROM EstudianteCarrera ec JOIN ec.carrera c " +
           "WHERE ec.graduacion > 0 " +
           "GROUP BY c.nombreCarrera, ec.graduacion " +
           "ORDER BY c.nombreCarrera ASC, ec.graduacion ASC")
    List<CarreraReporteDTO> getEgresadosPorAnio();
}
