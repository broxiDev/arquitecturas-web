package com.microservices.msvc.estudiantes.utils;

import com.microservices.msvc.estudiantes.entities.Estudiante;
import com.microservices.msvc.estudiantes.repositories.EstudianteRepository;
import com.microservices.msvc.estudiantes.utils.csv.CsvLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Component
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Transactional
    public void cargarDatos() throws IOException {
        if (!estudianteRepository.findAll().isEmpty()) {
            logger.info("Estudiantes ya cargados, omitiendo carga.");
            return;
        }
        logger.info("=== Cargando estudiantes desde CSV ===");
        List<Estudiante> estudiantes = CsvLoader.cargarEstudiantes();
        for (Estudiante e : estudiantes) {
            estudianteRepository.save(e);
        }
        logger.info("✓ Estudiantes cargados: {}", estudiantes.size());
        logger.info("=== Carga completada ===");
    }
}
