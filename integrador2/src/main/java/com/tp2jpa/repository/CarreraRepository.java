package com.tp2jpa.repository;

import com.tp2jpa.dto.CarreraInscriptosDTO;
import com.tp2jpa.dto.CarreraReporteDTO;
import com.tp2jpa.entities.Carrera;
import com.tp2jpa.factory.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Comparator;
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
            List<CarreraReporteDTO> porInscripcion = em.createQuery(
                    "SELECT new com.tp2jpa.dto.CarreraReporteDTO(c.nombreCarrera, ec.inscripcion, COUNT(ec), 0L) " +
                    "FROM EstudianteCarrera ec JOIN ec.carrera c " +
                    "WHERE ec.inscripcion > 0 " +
                    "GROUP BY c.nombreCarrera, ec.inscripcion " +
                    "ORDER BY c.nombreCarrera ASC, ec.inscripcion ASC",
                    CarreraReporteDTO.class
            ).getResultList();

            List<CarreraReporteDTO> porGraduacion = em.createQuery(
                    "SELECT new com.tp2jpa.dto.CarreraReporteDTO(c.nombreCarrera, ec.graduacion, 0L, COUNT(ec)) " +
                    "FROM EstudianteCarrera ec JOIN ec.carrera c " +
                    "WHERE ec.graduacion > 0 " +
                    "GROUP BY c.nombreCarrera, ec.graduacion " +
                    "ORDER BY c.nombreCarrera ASC, ec.graduacion ASC",
                    CarreraReporteDTO.class
            ).getResultList();

            // base: arrancamos con los inscriptos
            List<CarreraReporteDTO> reporte = new ArrayList<>(porInscripcion);

            // por cada fila de egresados, buscamos si ya existe esa carrera+año y sumamos
            for (CarreraReporteDTO egresado : porGraduacion) {
                boolean encontrado = false;
                for (int i = 0; i < reporte.size(); i++) {
                    CarreraReporteDTO existente = reporte.get(i);
                    if (existente.getCarrera().equals(egresado.getCarrera()) &&
                        existente.getAnio().equals(egresado.getAnio())) {
                        reporte.set(i, new CarreraReporteDTO(
                                existente.getCarrera(),
                                existente.getAnio(),
                                existente.getInscriptos(),
                                egresado.getEgresados()
                        ));
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    reporte.add(egresado);
                }
            }

            // ordenar por carrera y año
            reporte.sort(new Comparator<CarreraReporteDTO>() {
                @Override
                public int compare(CarreraReporteDTO a, CarreraReporteDTO b) {
                    int cmp = a.getCarrera().compareTo(b.getCarrera());
                    if (cmp != 0) return cmp;
                    return a.getAnio().compareTo(b.getAnio());
                }
            });

            return reporte;
        } finally {
            em.close();
        }
    }
}
