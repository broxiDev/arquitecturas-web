package com.tp2jpa.repository;

import com.tp2jpa.entities.Carrera;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.entities.EstudianteCarrera;
import com.tp2jpa.factory.JPAUtil;
import jakarta.persistence.EntityManager;

/**
 * Repositorio de operaciones sobre la entidad intermedia estudiante-carrera.
 */
public class EstudianteCarreraRepository {

    /**
     * Matricula un estudiante en una carrera.
     *
     * @param estudiante estudiante a matricular
     * @param carrera carrera destino
     * @param inscripcion año de inscripcion
     * @param graduacion año de graduacion
     * @param antiguedad antiguedad inicial en carrera
     */
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
