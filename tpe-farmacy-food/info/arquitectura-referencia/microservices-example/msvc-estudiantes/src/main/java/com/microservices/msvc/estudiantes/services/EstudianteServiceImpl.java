package com.microservices.msvc.estudiantes.services;

import com.microservices.msvc.estudiantes.entities.Estudiante;
import com.microservices.msvc.estudiantes.repositories.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EstudianteServiceImpl implements EstudianteService {

    @Autowired
    private EstudianteRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Estudiante> findAll() throws Exception {
        return repository.findAllByOrderByApellidoAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estudiante> findById(Long dni) throws Exception {
        return repository.findById(dni);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estudiante> findByLu(Long lu) throws Exception {
        return repository.findByLu(lu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estudiante> findByGenero(String genero) throws Exception {
        return repository.findByGeneroOrderByApellidoAsc(genero);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estudiante> findBulk(List<Long> dnis) throws Exception {
        return repository.findByDniIn(dnis);
    }

    @Override
    @Transactional
    public Estudiante save(Estudiante entity) throws Exception {
        return repository.save(entity);
    }
}
