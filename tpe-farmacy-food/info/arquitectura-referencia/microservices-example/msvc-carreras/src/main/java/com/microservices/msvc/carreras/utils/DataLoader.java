package com.microservices.msvc.carreras.utils;

import com.microservices.msvc.carreras.entities.Carrera;
import com.microservices.msvc.carreras.entities.EstudianteCarrera;
import com.microservices.msvc.carreras.repositories.CarreraRepository;
import com.microservices.msvc.carreras.repositories.EstudianteCarreraRepository;
import com.microservices.msvc.carreras.utils.csv.CsvLoader;
import com.microservices.msvc.carreras.utils.csv.CsvLoader.CarreraRaw;
import com.microservices.msvc.carreras.utils.csv.CsvLoader.EstudianteCarreraRaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private EstudianteCarreraRepository ecRepository;

    @Transactional
    public void cargarDatos() throws IOException {
        if (!carreraRepository.findAll().isEmpty()) {
            logger.info("Carreras ya cargadas, omitiendo carga.");
            return;
        }

        logger.info("=== Cargando carreras e inscripciones desde CSV ===");

        List<CarreraRaw> carrerasRaw = CsvLoader.cargarCarreras();
        Map<Long, Carrera> carrerasMap = new HashMap<>();
        for (CarreraRaw raw : carrerasRaw) {
            Carrera c = new Carrera();
            c.setNombreCarrera(raw.nombre());
            c.setDuracion(raw.duracion());
            Carrera saved = carreraRepository.save(c);
            carrerasMap.put(raw.csvId(), saved);
        }

        List<EstudianteCarreraRaw> raws = CsvLoader.cargarEstudianteCarreras();
        int insertadas = 0;
        for (EstudianteCarreraRaw raw : raws) {
            Carrera car = carrerasMap.get(raw.idCarreraCsv());
            if (car == null) {
                logger.warn("Carrera CSV id={} no encontrada, fila ignorada.", raw.idCarreraCsv());
                continue;
            }
            EstudianteCarrera ec = new EstudianteCarrera(
                    raw.dniEstudiante(), car,
                    raw.inscripcion(), raw.graduacion(), raw.antiguedad());
            ecRepository.save(ec);
            insertadas++;
        }

        logger.info("✓ Carreras: {}", carrerasRaw.size());
        logger.info("✓ Inscripciones: {}/{}", insertadas, raws.size());
        logger.info("=== Carga completada ===");
    }
}
