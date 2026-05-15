package com.tp2jpa.servicios;

import com.tp2jpa.dto.CarreraInscriptosDTO;
import com.tp2jpa.dto.CarreraReporteDTO;
import com.tp2jpa.entities.Carrera;
import com.tp2jpa.repository.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para operaciones sobre `Carrera`.
 *
 * Proporciona métodos CRUD básicos y consultas agregadas para generar
 * listados de carreras con cantidad de inscriptos y reportes anuales.
 */
@Service("CarreraServicio")
public class CarreraServicio implements BaseService<Carrera> {

    @Autowired
    private CarreraRepository carreraRepository;

    @Override
    @Transactional
    public List<Carrera> findAll() throws Exception {
        try {
            return carreraRepository.findAll();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Carrera findById(Long id) throws Exception {
        try {
            Optional<Carrera> result = carreraRepository.findById(id);
            return result.get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Carrera save(Carrera entity) throws Exception {
        try {
            return carreraRepository.save(entity);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Carrera update(Long id, Carrera entity) throws Exception {
        try {
            Optional<Carrera> result = carreraRepository.findById(id);
            Carrera existente = result.get();
            existente.setNombreCarrera(entity.getNombreCarrera());
            existente.setDuracion(entity.getDuracion());
            return carreraRepository.save(existente);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) throws Exception {
        try {
            if (carreraRepository.existsById(id)) {
                carreraRepository.deleteById(id);
                return true;
            } else {
                throw new Exception("Carrera no encontrada");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<CarreraInscriptosDTO> getCarrerasConInscriptos() throws Exception {
        try {
            return carreraRepository.getCarrerasConInscriptos();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<CarreraReporteDTO> getReporteAnual() throws Exception {
        try {
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

            reporte.sort(new Comparator<CarreraReporteDTO>() {
                @Override
                public int compare(CarreraReporteDTO a, CarreraReporteDTO b) {
                    int cmp = a.getCarrera().compareTo(b.getCarrera());
                    if (cmp != 0) return cmp;
                    return a.getAnio().compareTo(b.getAnio());
                }
            });

            return reporte;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
