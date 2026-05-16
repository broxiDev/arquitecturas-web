package micro.example.repository;

import com.opencsv.CSVReader;
import jakarta.persistence.EntityManager;
import micro.example.dto.PersonaDTO;
import micro.example.factory.JPAUtil;
import micro.example.modelo.Direccion;
import micro.example.modelo.Persona;

import java.io.FileReader;
import java.util.List;

public class PersonaRepository {

    public void insertarDesdeCSV(String rutaArchivo) {
        EntityManager em = JPAUtil.getEntityManager();
        try (CSVReader reader = new CSVReader(new FileReader(rutaArchivo))) {
            String[] linea;
            reader.readNext(); // salta cabecera

            em.getTransaction().begin();

            while ((linea = reader.readNext()) != null) {
                Persona persona = new Persona();
                persona.setNombre(linea[1]);
                persona.setEdad(Integer.parseInt(linea[2]));
                persona.setMail(linea[3]);

                // Busco la Direccion por ID
                Direccion direccion = em.find(Direccion.class, Integer.parseInt(linea[4]));
                persona.setDireccion(direccion);

                em.persist(persona);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Persona> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Persona> personas = em.createQuery("SELECT p FROM Persona p", Persona.class).getResultList();
        em.close();
        return personas;
    }

    public List<Persona> buscarPorCiudad(String ciudad) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Persona> personas = em.createQuery(
                        "SELECT p FROM Persona p JOIN p.direccion d WHERE d.ciudad = :ciudad", Persona.class)
                .setParameter("ciudad", ciudad)
                .getResultList();
        em.close();
        return personas;
    }

    public List<PersonaDTO> buscarPorCiudad2(String ciudad) {
        EntityManager em = JPAUtil.getEntityManager();

        List<PersonaDTO> personasDTO = em.createQuery(
                        "SELECT new micro.example.dto.PersonaDTO(p.nombre, p.edad, d.ciudad, d.calle, d.numero) " +
                                "FROM Persona p JOIN p.direccion d " +
                                "WHERE d.ciudad = :ciudad",
                        PersonaDTO.class
                )
                .setParameter("ciudad", ciudad)
                .getResultList();

        em.close();
        return personasDTO;
    }


}