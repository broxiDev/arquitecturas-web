package com.tp2jpa.servicios;

import com.tp2jpa.entities.Carrera;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.entities.EstudianteCarrera;
import com.tp2jpa.repository.CarreraRepository;
import com.tp2jpa.repository.EstudianteCarreraRepository;
import com.tp2jpa.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que implementa las operaciones de negocio referentes a estudiantes.
 *
 * Provee métodos para listar, buscar por LU, generar consultas por género y
 * matricular estudiantes en carreras. Esta clase es transaccional y usa
 * repositorios JPA para persistencia.
 */
@Service("EstudianteServicio")
public class EstudianteServicio implements BaseService<Estudiante> {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private EstudianteCarreraRepository ecRepository;

    @Override
    @Transactional
    public List<Estudiante> findAll() throws Exception {
        try {
            return estudianteRepository.findAllByOrderByApellidoAsc();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Estudiante findById(Long id) throws Exception {
        try {
            Optional<Estudiante> result = estudianteRepository.findById(id);
            return result.get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Estudiante save(Estudiante entity) throws Exception {
        try {
            return estudianteRepository.save(entity);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Estudiante update(Long id, Estudiante entity) throws Exception {
        try {
            Optional<Estudiante> result = estudianteRepository.findById(id);
            Estudiante existente = result.get();
            existente.setNombre(entity.getNombre());
            existente.setApellido(entity.getApellido());
            existente.setEdad(entity.getEdad());
            existente.setGenero(entity.getGenero());
            existente.setCiudad(entity.getCiudad());
            existente.setLu(entity.getLu());
            return estudianteRepository.save(existente);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) throws Exception {
        try {
            if (estudianteRepository.existsById(id)) {
                estudianteRepository.deleteById(id);
                return true;
            } else {
                throw new Exception("Estudiante no encontrado");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public Estudiante buscarPorLU(Long lu) throws Exception {
        try {
            Optional<Estudiante> result = estudianteRepository.findByLu(lu);
            return result.get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<Estudiante> buscarPorGenero(String genero) throws Exception {
        try {
            return estudianteRepository.findByGeneroOrderByApellidoAsc(genero);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<Estudiante> buscarPorCarreraYCiudad(String carrera, String ciudad) throws Exception {
        try {
            return estudianteRepository.findByCarreraAndCiudad(carrera, ciudad);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public EstudianteCarrera matricular(Long dni, Long idCarrera, int inscripcion, int graduacion, int antiguedad) throws Exception {
        try {
            Optional<Estudiante> estOpt = estudianteRepository.findById(dni);
            Optional<Carrera> carOpt = carreraRepository.findById(idCarrera);
            Estudiante estudiante = estOpt.get();
            Carrera carrera = carOpt.get();
            EstudianteCarrera ec = new EstudianteCarrera(carrera, inscripcion, graduacion, antiguedad);
            ec.setEstudiante(estudiante);
            return ecRepository.save(ec);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
