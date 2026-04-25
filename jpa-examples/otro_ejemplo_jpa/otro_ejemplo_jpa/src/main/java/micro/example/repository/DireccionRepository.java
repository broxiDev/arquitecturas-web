package micro.example.repository;


import com.opencsv.CSVReader;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import micro.example.dto.DireccionDTO;
import micro.example.dto.PersonaDTO;
import micro.example.factory.JPAUtil;
import micro.example.modelo.Direccion;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class DireccionRepository {


    public void insertarDesdeCSV(String rutaArchivo) {
        EntityManager em = JPAUtil.getEntityManager();

        try (CSVReader reader = new CSVReader(new FileReader(rutaArchivo))) {
            String[] linea;
            reader.readNext(); // salta cabecera

            em.getTransaction().begin();

            while ((linea = reader.readNext()) != null) {
                Direccion direccion = new Direccion();
                direccion.setCiudad(linea[1]);
                direccion.setCalle(linea[2]);
                direccion.setNumero(Integer.parseInt(linea[3]));
                direccion.setCodigoPostal(linea[4]);

                em.persist(direccion);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<DireccionDTO> direccion_de_persona(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        List<DireccionDTO> direcciones = new ArrayList<>();

        try {
            direcciones = em.createQuery(
                            "SELECT new micro.example.dto.DireccionDTO(d.ciudad, d.calle, d.numero, d.codigoPostal) " +
                                    "FROM Direccion d JOIN d.personas p " +
                                    "WHERE p.nombre = :nombre",
                            DireccionDTO.class
                    )
                    .setParameter("nombre", nombre)
                    .getResultList();

            if (direcciones.isEmpty()) {
                System.out.println("No existe persona con ese nombre");
            }

        } catch (Exception e) {
            System.out.println("Error al ejecutar la consulta: " + e.getMessage());
        } finally {
            em.close();
        }

        return direcciones;
    }

}

