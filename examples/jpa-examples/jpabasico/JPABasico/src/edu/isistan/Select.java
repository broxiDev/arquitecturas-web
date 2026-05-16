package edu.isistan;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import edu.isistan.dao.Direccion;
import edu.isistan.dao.Persona;

public class Select {

	public static void main(String[] args) {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Example");
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		Persona j = em.find(Persona.class, 1);
		System.out.println(j);
		
		@SuppressWarnings("unchecked")
//		String sql = "SELECT * FROM persona";
		
//		String sql = "SELECT p.anios FROM persona p";
		
		List<Persona> personas = em.createQuery("SELECT p.edad FROM Persona p").getResultList();
//		personas.forEach(p-> System.out.println(p));
		
		for (Persona p: personas)
			System.out.println(p);
		
		em.getTransaction().commit();
		em.close();
		emf.close();
	}
	
}
