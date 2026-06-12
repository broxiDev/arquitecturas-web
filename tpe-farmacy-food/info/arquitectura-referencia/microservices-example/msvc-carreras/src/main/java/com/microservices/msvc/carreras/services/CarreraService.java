package com.microservices.msvc.carreras.services;

import com.microservices.msvc.carreras.dto.CarreraInscriptosDTO;
import com.microservices.msvc.carreras.dto.CarreraReporteDTO;
import com.microservices.msvc.carreras.entities.EstudianteCarrera;
import com.microservices.msvc.carreras.models.Estudiante;

import java.util.List;

public interface CarreraService {
    List<CarreraInscriptosDTO> getCarrerasConInscriptos() throws Exception;
    List<CarreraReporteDTO> getReporteAnual() throws Exception;
    EstudianteCarrera matricular(Long dni, Long idCarrera, int inscripcion, int graduacion, int antiguedad) throws Exception;
    List<Estudiante> buscarPorCarreraYCiudad(String carrera, String ciudad) throws Exception;
}
