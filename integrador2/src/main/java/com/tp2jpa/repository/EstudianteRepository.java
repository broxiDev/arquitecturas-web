package com.tp2jpa.repository;

import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.factory.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Repositorio de Estudiante.
 */
public class EstudianteRepository {

    /**
     * Persiste un estudiante.
     *
     * @param estudiante estudiante a guardar
     */
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

    /**
     * Recupera todos los estudiantes ordenados por apellido.
     *
     * @return lista de estudiantes
     */
    public List<Estudiante> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Estudiante e ORDER BY e.apellido ASC",
                    Estudiante.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Recupera un estudiante por numero de libreta universitaria.
     *
     * @param lu numero de libreta
     * @return estudiante encontrado o null si no existe
     */
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

    /**
     * Recupera todos los estudiantes de un genero.
     *
     * @param genero genero de filtro
     * @return lista de estudiantes
     */
    public List<Estudiante> buscarPorGenero(String genero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Estudiante e WHERE e.genero = :genero ORDER BY e.apellido ASC",
                    Estudiante.class
            ).setParameter("genero", genero).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Recupera estudiantes de una carrera filtrados por ciudad.
     *
     * @param carrera nombre de la carrera
     * @param ciudad ciudad de residencia
     * @return lista de estudiantes
     */
    public List<Estudiante> buscarPorCarreraYCiudad(String carrera, String ciudad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Estudiante e JOIN e.inscripciones i JOIN i.carrera c " +
                    "WHERE c.nombreCarrera = :carrera AND e.ciudad = :ciudad " +
                    "ORDER BY e.apellido ASC",
                    Estudiante.class
            ).setParameter("carrera", carrera)
             .setParameter("ciudad", ciudad)
             .getResultList();
        } finally {
            em.close();
        }
    }
}
