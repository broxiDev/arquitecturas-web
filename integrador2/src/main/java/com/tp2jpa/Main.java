package com.tp2jpa;

import com.tp2jpa.entities.Carrera;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.factory.JPAUtil;
import com.tp2jpa.repository.CarreraRepository;
import com.tp2jpa.repository.EstudianteCarreraRepository;
import com.tp2jpa.repository.EstudianteRepository;
import com.tp2jpa.utils.DataLoader;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        //poblarDB(); // ejecutar solo una vez para cargar CSV

        System.out.println("\n=== 2a — Alta de estudiante ===");
        //darDeAltaEstudiante();

        System.out.println("\n=== 2b — Matricular en carrera ===");
        //matricularEstudiante();

        System.out.println("\n=== 2c — Todos los estudiantes (por apellido) ===");
        listarTodosLosEstudiantes();

    }

    // 2a — Dar de alta un estudiante
    private static void darDeAltaEstudiante() {
        EstudianteRepository repo = new EstudianteRepository();

        Estudiante nuevo = new Estudiante(
                99999999L,
                "Nahuel",
                "Di Fiore",
                25,
                "Masculino",
                "Lobercity",
                999001L
        );

        repo.guardar(nuevo);
        System.out.println("Estudiante dado de alta: " + nuevo);
    }

    // 2b — Matricular un estudiante en una carrera
    private static void matricularEstudiante() {
        EstudianteRepository estudianteRepo = new EstudianteRepository();
        CarreraRepository carreraRepo = new CarreraRepository();
        EstudianteCarreraRepository inscripcionRepo = new EstudianteCarreraRepository();

        Estudiante estudiante = estudianteRepo.buscarPorLU(999001L);
        Carrera carrera = carreraRepo.buscarPorNombre("TUDAI");

        if (estudiante == null || carrera == null) {
            System.out.println("Estudiante o carrera no encontrados.");
            return;
        }

        inscripcionRepo.matricular(estudiante, carrera, 2024, 0, 1);
        System.out.println("Matriculado: " + estudiante.getNombre() + " en " + carrera.getNombreCarrera());
    }

    // 2c — Listar todos los estudiantes ordenados por apellido
    private static void listarTodosLosEstudiantes() {
        EstudianteRepository repo = new EstudianteRepository();
        List<Estudiante> estudiantes = repo.buscarTodos();
        System.out.println("Total: " + estudiantes.size() + " estudiantes");
        for (Estudiante e : estudiantes) {
            System.out.println(e);
        }
    }

    private static void poblarDB() {
        DataLoader.inicializarMetadata();
    }
}
