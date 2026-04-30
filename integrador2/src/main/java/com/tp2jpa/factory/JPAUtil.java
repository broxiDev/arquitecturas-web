package com.tp2jpa.factory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utilidad para gestionar el ciclo de vida de JPA.
 */
public class JPAUtil {

    /** Fabrica singleton de EntityManager. */
    private static final EntityManagerFactory emf;

    static {
        emf = Persistence.createEntityManagerFactory("integrador2-mysql");
    }

    /**
     * Crea un nuevo EntityManager.
     *
     * @return entity manager nuevo
     */
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Cierra la fabrica de entity managers si esta abierta.
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
