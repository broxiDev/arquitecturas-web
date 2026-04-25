package com.tp2jpa.repository;

import com.tp2jpa.dto.CarreraInscriptosDTO;
import com.tp2jpa.dto.CarreraReporteDTO;
import com.tp2jpa.entities.Carrera;
import com.tp2jpa.factory.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CarreraRepository {

    // Alta de carrera
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

    // Buscar carrera por nombre (util para matricular)
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

    // 2f — Carreras con inscriptos ordenadas por cantidad DESC
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

    // Punto 3 — Reporte: inscriptos y egresados por carrera y año, ordenado alfabético y cronológico
    public List<CarreraReporteDTO> generarReporte() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT new com.tp2jpa.dto.CarreraReporteDTO(" +
                    "  c.nombreCarrera, " +
                    "  i.antiguedad, " +
                    "  COUNT(i), " +
                    "  SUM(CASE WHEN i.graduacion > 0 THEN 1L ELSE 0L END)" +
                    ") " +
                    "FROM Carrera c JOIN c.inscripciones i " +
                    "GROUP BY c.nombreCarrera, i.antiguedad " +
                    "ORDER BY c.nombreCarrera ASC, i.antiguedad ASC",
                    CarreraReporteDTO.class
            ).getResultList();
        } finally {
            em.close();
        }
    }
}
