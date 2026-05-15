package com.tp2jpa.utils;

import com.tp2jpa.entities.Carrera;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.entities.EstudianteCarrera;
import com.tp2jpa.repository.CarreraRepository;
import com.tp2jpa.repository.EstudianteCarreraRepository;
import com.tp2jpa.repository.EstudianteRepository;
import com.tp2jpa.utils.csv.CsvLoader;
import com.tp2jpa.utils.csv.CsvLoader.CarreraRaw;
import com.tp2jpa.utils.csv.CsvLoader.EstudianteCarreraRaw;
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
/**
 * Utilidad para cargar datos iniciales desde CSV al iniciar la aplicación.
 *
 * Se usa en ambientes de desarrollo para poblar las tablas `carrera`,
 * `estudiante` y `estudiante_carrera` si la base de datos está vacía.
 */
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private EstudianteCarreraRepository ecRepository;

    @Transactional
    /**
     * Carga datos desde los CSV ubicados en `src/main/resources` usando `CsvLoader`.
     * Omite la carga si ya existen carreras en la base de datos.
     */
    public void cargarDatos() throws IOException {
        if (!carreraRepository.findAll().isEmpty()) {
            logger.info("Base de datos ya contiene datos, omitiendo carga.");
            return;
        }

        logger.info("=== Iniciando carga de datos desde CSV ===");

        List<CarreraRaw> carrerasRaw = CsvLoader.cargarCarreras();
        Map<Long, Carrera> carrerasMap = new HashMap<>();
        for (CarreraRaw raw : carrerasRaw) {
            Carrera c = new Carrera();
            c.setNombreCarrera(raw.nombre());
            c.setDuracion(raw.duracion());
            Carrera saved = carreraRepository.save(c);
            carrerasMap.put(raw.csvId(), saved);
        }

        List<Estudiante> estudiantes = CsvLoader.cargarEstudiantes();
        Map<Long, Estudiante> estudiantesMap = new HashMap<>();
        for (Estudiante e : estudiantes) {
            Estudiante saved = estudianteRepository.save(e);
            estudiantesMap.put(saved.getDni(), saved);
        }

        List<EstudianteCarreraRaw> raws = CsvLoader.cargarEstudianteCarreras();
        int ecInsertadas = 0;
        for (EstudianteCarreraRaw raw : raws) {
            Carrera car = carrerasMap.get(raw.idCarreraCsv());
            Estudiante est = estudiantesMap.get(raw.dniEstudiante());
            if (car == null || est == null) {
                logger.warn("Fila ignorada: estudiante={} o carrera={} no encontrada",
                        raw.dniEstudiante(), raw.idCarreraCsv());
                continue;
            }
            EstudianteCarrera ec = new EstudianteCarrera(car, raw.inscripcion(), raw.graduacion(), raw.antiguedad());
            ec.setEstudiante(est);
            ecRepository.save(ec);
            ecInsertadas++;
        }

        logger.info("✓ Carreras: {}", carrerasRaw.size());
        logger.info("✓ Estudiantes: {}", estudiantes.size());
        logger.info("✓ Inscripciones: {}/{}", ecInsertadas, raws.size());
        logger.info("=== Carga completada ===");
    }
}
