package com.microservices.msvc.carreras.services;

import com.microservices.msvc.carreras.clients.EstudianteClientRest;
import com.microservices.msvc.carreras.dto.CarreraInscriptosDTO;
import com.microservices.msvc.carreras.dto.CarreraReporteDTO;
import com.microservices.msvc.carreras.entities.Carrera;
import com.microservices.msvc.carreras.entities.EstudianteCarrera;
import com.microservices.msvc.carreras.models.Estudiante;
import com.microservices.msvc.carreras.repositories.CarreraRepository;
import com.microservices.msvc.carreras.repositories.EstudianteCarreraRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarreraServiceImpl implements CarreraService {

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private EstudianteCarreraRepository ecRepository;

    @Autowired
    private EstudianteClientRest estudianteClient;

    @Override
    @Transactional(readOnly = true)
    public List<CarreraInscriptosDTO> getCarrerasConInscriptos() throws Exception {
        return carreraRepository.getCarrerasConInscriptos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraReporteDTO> getReporteAnual() throws Exception {
        List<CarreraReporteDTO> porInscripcion = carreraRepository.getInscriptosPorAnio();
        List<CarreraReporteDTO> porGraduacion = carreraRepository.getEgresadosPorAnio();

        List<CarreraReporteDTO> reporte = new ArrayList<>(porInscripcion);

        for (CarreraReporteDTO egresado : porGraduacion) {
            boolean encontrado = false;
            for (int i = 0; i < reporte.size(); i++) {
                CarreraReporteDTO existente = reporte.get(i);
                if (existente.getCarrera().equals(egresado.getCarrera()) &&
                    existente.getAnio().equals(egresado.getAnio())) {
                    reporte.set(i, new CarreraReporteDTO(
                            existente.getCarrera(),
                            existente.getAnio(),
                            existente.getInscriptos(),
                            egresado.getEgresados()
                    ));
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                reporte.add(egresado);
            }
        }

        reporte.sort(Comparator.comparing(CarreraReporteDTO::getCarrera)
                               .thenComparing(CarreraReporteDTO::getAnio));
        return reporte;
    }

    @Override
    @Transactional
    public EstudianteCarrera matricular(Long dni, Long idCarrera, int inscripcion, int graduacion, int antiguedad) throws Exception {
        // Valida que el estudiante existe en msvc-estudiantes via Feign
        estudianteClient.getEstudiante(dni);

        Optional<Carrera> carOpt = carreraRepository.findById(idCarrera);
        if (carOpt.isEmpty()) {
            throw new Exception("Carrera no encontrada: " + idCarrera);
        }
        EstudianteCarrera ec = new EstudianteCarrera(dni, carOpt.get(), inscripcion, graduacion, antiguedad);
        return ecRepository.save(ec);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estudiante> buscarPorCarreraYCiudad(String carrera, String ciudad) throws Exception {
        List<EstudianteCarrera> inscripciones = ecRepository.findByCarrera_NombreCarrera(carrera);

        List<Long> dnis = inscripciones.stream()
                .map(EstudianteCarrera::getDniEstudiante)
                .distinct()
                .collect(Collectors.toList());

        if (dnis.isEmpty()) {
            return List.of();
        }

        List<Estudiante> estudiantes = estudianteClient.getEstudiantesBulk(dnis);

        return estudiantes.stream()
                .filter(e -> ciudad.equalsIgnoreCase(e.getCiudad()))
                .sorted(Comparator.comparing(Estudiante::getApellido))
                .collect(Collectors.toList());
    }
}
