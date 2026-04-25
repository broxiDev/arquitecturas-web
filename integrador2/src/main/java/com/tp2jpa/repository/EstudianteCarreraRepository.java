package com.tp2jpa.repository;

import com.tp2jpa.entities.Carrera;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.entities.EstudianteCarrera;
import com.tp2jpa.factory.JPAUtil;
import jakarta.persistence.EntityManager;

public class EstudianteCarreraRepository {

    // 2b — Matricular un estudiante en una carrera
    public void matricular(Estudiante estudiante, Carrera carrera, int inscripcion, int graduacion, int antiguedad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Estudiante estManaged = em.find(Estudiante.class, estudiante.getDni());
            Carrera carManaged = em.find(Carrera.class, carrera.getIdCarrera());

            EstudianteCarrera ec = new EstudianteCarrera(carManaged, inscripcion, graduacion, antiguedad);
            ec.setEstudiante(estManaged);
            em.persist(ec);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
