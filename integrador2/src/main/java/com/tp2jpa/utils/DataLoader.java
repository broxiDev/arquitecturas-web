package com.tp2jpa.utils;

import com.tp2jpa.entities.Carrera;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.entities.EstudianteCarrera;
import com.tp2jpa.factory.JPAUtil;
import com.tp2jpa.utils.csv.CsvLoader;
import com.tp2jpa.utils.csv.CsvLoader.CarreraRaw;
import com.tp2jpa.utils.csv.CsvLoader.EstudianteCarreraRaw;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLoader {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    // IMPORTANTE: ejecutar solo 1 vez, luego comentar la llamada en Main.
    public static void inicializarMetadata() {
        logger.info("=== Iniciando carga de datos desde CSV ===");
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Carreras — csvId se usa solo para el mapeo en memoria
            List<CarreraRaw> carrerasRaw = CsvLoader.cargarCarreras();
            Map<Long, Carrera> carrerasMap = new HashMap<>();
            for (CarreraRaw raw : carrerasRaw) {
                Carrera c = new Carrera();
                c.setNombreCarrera(raw.nombre());
                c.setDuracion(raw.duracion());
                em.persist(c);
                carrerasMap.put(raw.csvId(), c);
            }

            // 2. Estudiantes (DNI es clave natural, se toma del CSV)
            List<Estudiante> estudiantes = CsvLoader.cargarEstudiantes();
            Map<Long, Estudiante> estudiantesMap = new HashMap<>();
            for (Estudiante e : estudiantes) {
                em.persist(e);
                estudiantesMap.put(e.getDni(), e);
            }

            // 3. EstudianteCarrera (depende de Carrera y Estudiante)
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
                em.persist(ec);
                ecInsertadas++;
            }

            em.getTransaction().commit();
            logger.info("=== Resumen de carga ===");
            logger.info("✓ Carreras insertadas: {}", carrerasRaw.size());
            logger.info("✓ Estudiantes insertados: {}", estudiantes.size());
            logger.info("✓ Inscripciones insertadas: {}/{}", ecInsertadas, raws.size());
            logger.info("=== Carga completada exitosamente ===");

        } catch (IOException e) {
            em.getTransaction().rollback();
            logger.error("Error al leer CSV: {}", e.getMessage(), e);
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.error("Error durante la carga: {}", e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
