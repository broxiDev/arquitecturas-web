package micro.example;

import micro.example.dto.DireccionDTO;
import micro.example.dto.PersonaDTO;
import micro.example.modelo.Direccion;
import micro.example.modelo.Persona;
import micro.example.repository.DireccionRepository;
import micro.example.repository.PersonaRepository;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DireccionRepository dr = new DireccionRepository();
        PersonaRepository pr = new PersonaRepository();

       dr.insertarDesdeCSV("src/main/resources/direccion.csv");
       pr.insertarDesdeCSV("src/main/resources/persona.csv");

        for(Persona persona : pr.buscarTodos()) {
            System.out.println(persona.getNombre() + " vive en " + persona.getDireccion().getCiudad() + ", "+ persona.getDireccion().getCalle() + " "+  persona.getDireccion().getNumero());
        }

        System.out.println("-----------------------------------");
        System.out.println("-----------------------------------");

//      Sin usar DTO MALA PRACTICA !!!
        List<Persona> personas_por_ciudad = pr.buscarPorCiudad("Bahia Blanca");
        for (Persona p : personas_por_ciudad){
            System.out.println(p.getNombre() + " vive en " + p.getDireccion().getCiudad() + ", "+ p.getDireccion().getCalle() + " "+  p.getDireccion().getNumero());
        }
        System.out.println("-----------------------------------");
        System.out.println("-----------------------------------");

//        Usando DTO como debe ser !!!
        List<PersonaDTO> personas_por_ciudadDTO = pr.buscarPorCiudad2("Bahia Blanca");
        for (PersonaDTO p : personas_por_ciudadDTO){
            System.out.println(p);
        }

        System.out.println("-----------------------------------");
        System.out.println("-----------------------------------");

//        Una consulta para utilizar DireccionDTO
        String nombre = "Pedro Pons";
        List<DireccionDTO> direccion = dr.direccion_de_persona(nombre);
        System.out.println("La direccion de "+nombre+" es "+direccion);
    }
}