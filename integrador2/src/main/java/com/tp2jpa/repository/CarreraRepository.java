package com.tp2jpa.repository;

import com.tp2jpa.dto.CarreraInscriptosDTO;
import com.tp2jpa.dto.CarreraReporteDTO;
import com.tp2jpa.entities.Carrera;
import com.tp2jpa.factory.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de Carrera.
 * Centraliza operaciones de persistencia y consultas sobre carreras.
 */
public class CarreraRepository {

    /**
     * Guarda una carrera en la base de datos.
     *
     * @param carrera entidad a persistir
     */
    public void guardar(Carrera carrera) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(carrera);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Busca una carrera por su nombre.
     *
     * @param nombre nombre de la carrera
     * @return la carrera encontrada o null si no existe
     */
    public Carrera buscarPorNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Carrera c WHERE c.nombreCarrera = :nombre",
                    Carrera.class
            ).setParameter("nombre", nombre).getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Recupera carreras con estudiantes inscriptos, ordenadas por cantidad descendente.
     *
     * @return lista de carrera con cantidad de inscriptos
     */
    public List<CarreraInscriptosDTO> buscarCarrerasConInscriptos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT new com.tp2jpa.dto.CarreraInscriptosDTO(c.nombreCarrera, COUNT(i)) " +
                    "FROM Carrera c JOIN c.inscripciones i " +
                    "GROUP BY c.idCarrera, c.nombreCarrera " +
                    "ORDER BY COUNT(i) DESC",
                    CarreraInscriptosDTO.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Genera el reporte de carreras con inscriptos y egresados por año.
     * El resultado se ordena por carrera (alfabetico) y año (cronologico).
     *  
     * @return lista de filas del reporte
     */
    public List<CarreraReporteDTO> generarReporte() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String sql = """
                    SELECT t.carrera, t.anio, SUM(t.inscriptos) AS inscriptos, SUM(t.egresados) AS egresados
                    FROM (
                        SELECT c.carrera AS carrera, ec.inscripcion AS anio, COUNT(*) AS inscriptos, 0 AS egresados
                        FROM carrera c
                        JOIN estudiante_carrera ec ON c.id_carrera = ec.id_carrera
                        WHERE ec.inscripcion IS NOT NULL AND ec.inscripcion > 0
                        GROUP BY c.carrera, ec.inscripcion

                        UNION ALL

                        SELECT c.carrera AS carrera, ec.graduacion AS anio, 0 AS inscriptos, COUNT(*) AS egresados
                        FROM carrera c
                        JOIN estudiante_carrera ec ON c.id_carrera = ec.id_carrera
                        WHERE ec.graduacion IS NOT NULL AND ec.graduacion > 0
                        GROUP BY c.carrera, ec.graduacion
                    ) t
                    GROUP BY t.carrera, t.anio
                    ORDER BY t.carrera ASC, t.anio ASC
                    """;

            @SuppressWarnings("unchecked")
            List<Object[]> filas = em.createNativeQuery(sql).getResultList();
            List<CarreraReporteDTO> reporte = new ArrayList<>();

            for (Object[] fila : filas) {
                String carrera = (String) fila[0];
                Integer anio = ((Number) fila[1]).intValue();
                Long inscriptos = ((Number) fila[2]).longValue();
                Long egresados = ((Number) fila[3]).longValue();
                reporte.add(new CarreraReporteDTO(carrera, anio, inscriptos, egresados));
            }

            return reporte;
        } finally {
            em.close();
        }
    }
}
