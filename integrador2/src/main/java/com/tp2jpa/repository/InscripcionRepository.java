package com.tp2jpa.repository;

import com.tp2jpa.entities.Carrera;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.entities.Inscripcion;
import com.tp2jpa.factory.JPAUtil;
import jakarta.persistence.EntityManager;

public class InscripcionRepository {

    // 2b — Matricular un estudiante en una carrera
    public void matricular(Estudiante estudiante, Carrera carrera, int antiguedadEnAnios, boolean graduado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Re-adjuntar entidades al contexto de persistencia actual
            Estudiante estManaged = em.find(Estudiante.class, estudiante.getId());
            Carrera carManaged = em.find(Carrera.class, carrera.getId());

            Inscripcion inscripcion = new Inscripcion(carManaged, antiguedadEnAnios, graduado);
            inscripcion.setEstudiante(estManaged);
            em.persist(inscripcion);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}

