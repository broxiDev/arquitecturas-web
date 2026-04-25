package com.tp2jpa.repository;

import com.tp2jpa.dto.EstudianteDTO;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.factory.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class EstudianteRepository {

    // 2a — Dar de alta un estudiante
    public void guardar(Estudiante estudiante) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(estudiante);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // 2c — Recuperar todos los estudiantes ordenados por apellido
    public List<EstudianteDTO> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT new com.tp2jpa.dto.EstudianteDTO(e.nombre, e.apellido, " +
                    "e.lu, e.genero, e.ciudad) " +
                    "FROM Estudiante e ORDER BY e.apellido ASC",
                    EstudianteDTO.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    // 2d — Recuperar un estudiante por número de libreta universitaria
    public Estudiante buscarPorLU(Long lu) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Estudiante e WHERE e.lu = :lu",
                    Estudiante.class
            ).setParameter("lu", lu).getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    // 2e — Recuperar todos los estudiantes por género
    public List<EstudianteDTO> buscarPorGenero(String genero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT new com.tp2jpa.dto.EstudianteDTO(e.nombre, e.apellido, " +
                    "e.lu, e.genero, e.ciudad) " +
                    "FROM Estudiante e WHERE e.genero = :genero ORDER BY e.apellido ASC",
                    EstudianteDTO.class
            ).setParameter("genero", genero).getResultList();
        } finally {
            em.close();
        }
    }

    // 2g — Recuperar estudiantes de una carrera filtrado por ciudad
    public List<EstudianteDTO> buscarPorCarreraYCiudad(String carrera, String ciudad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT new com.tp2jpa.dto.EstudianteDTO(e.nombre, e.apellido, " +
                    "e.lu, e.genero, e.ciudad) " +
                    "FROM Estudiante e JOIN e.inscripciones i JOIN i.carrera c " +
                    "WHERE c.nombreCarrera = :carrera AND e.ciudad = :ciudad " +
                    "ORDER BY e.apellido ASC",
                    EstudianteDTO.class
            ).setParameter("carrera", carrera)
             .setParameter("ciudad", ciudad)
             .getResultList();
        } finally {
            em.close();
        }
    }
}
