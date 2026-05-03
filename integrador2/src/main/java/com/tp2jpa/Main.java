package com.tp2jpa;

import com.tp2jpa.dto.CarreraInscriptosDTO;
import com.tp2jpa.dto.CarreraReporteDTO;
import com.tp2jpa.entities.Carrera;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.factory.JPAUtil;
import com.tp2jpa.repository.CarreraRepository;
import com.tp2jpa.repository.EstudianteCarreraRepository;
import com.tp2jpa.repository.EstudianteRepository;
import com.tp2jpa.utils.DataLoader;

import java.util.List;

/**
 * Punto de entrada para ejecutar casos de uso del integrador JPA.
 */
public class Main {

    public static void main(String[] args) {
        // ejecutar solo una vez para cargar CSV
        poblarDB();

        // 2a - Alta de estudiante
        //darDeAltaEstudiante();

        // 2b - Matricular en carrera
        //matricularEstudiante();

        // 2c - Todos los estudiantes (por apellido)
        //listarTodosLosEstudiantes();

        // 2d - Estudiante por libreta universitaria
        //buscarEstudiantePorLU(999001L);

        // 2e - Estudiantes por genero
        //listarEstudiantesPorGenero("Masculino");

        // 2f - Carreras con inscriptos
        //listarCarrerasConInscriptos();

        // 2g - Estudiantes por carrera y ciudad
        //listarEstudiantesPorCarreraYCiudad("Ingenieria Electronica", "Ganhe");

        // 3 - Reporte anual de carreras
        //generarReporteCarreras();

    }

    /**
     * Da de alta un estudiante de ejemplo.
     */
    private static void darDeAltaEstudiante() {
        System.out.println("\n=== 2a - Alta de estudiante ===");
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

    /**
     * Matricula un estudiante de ejemplo en una carrera.
     */
    private static void matricularEstudiante() {
        System.out.println("\n=== 2b - Matricular en carrera ===");
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

    /**
     * Lista todos los estudiantes ordenados por apellido.
     */
    private static void listarTodosLosEstudiantes() {
        System.out.println("\n=== 2c - Todos los estudiantes (por apellido) ===");
        EstudianteRepository repo = new EstudianteRepository();
        List<Estudiante> estudiantes = repo.buscarTodos();
        System.out.println("Total: " + estudiantes.size() + " estudiantes");
        for (Estudiante e : estudiantes) {
            System.out.println(e);
        }
    }

    /**
     * Busca y muestra un estudiante por LU.
     *
     * @param lu numero de libreta universitaria
     */
    private static void buscarEstudiantePorLU(Long lu) {
        System.out.println("\n=== 2d - Estudiante por libreta universitaria ===");
        EstudianteRepository repo = new EstudianteRepository();
        Estudiante estudiante = repo.buscarPorLU(lu);

        if (estudiante == null) {
            System.out.println("No se encontro estudiante con LU: " + lu);
            return;
        }

        System.out.println(estudiante);
    }

    /**
     * Lista estudiantes filtrados por genero.
     *
     * @param genero genero buscado
     */
    private static void listarEstudiantesPorGenero(String genero) {
        System.out.println("\n=== 2e - Estudiantes por genero ===");
        EstudianteRepository repo = new EstudianteRepository();
        List<Estudiante> estudiantes = repo.buscarPorGenero(genero);

        System.out.println("Total: " + estudiantes.size() + " estudiantes de genero " + genero);
        for (Estudiante e : estudiantes) {
            System.out.println(e);
        }
    }

    /**
     * Lista carreras con inscriptos ordenadas por cantidad.
     */
    private static void listarCarrerasConInscriptos() {
        System.out.println("\n=== 2f - Carreras con inscriptos ===");
        CarreraRepository repo = new CarreraRepository();
        List<CarreraInscriptosDTO> carreras = repo.buscarCarrerasConInscriptos();

        for (int i = 0; i < carreras.size(); i++) {
            CarreraInscriptosDTO dto = carreras.get(i);
            System.out.printf("  %d. %-30s %d inscriptos%n",
                    i + 1, dto.getNombre(), dto.getCantidadInscriptos());
        }
    }

    /**
     * Lista estudiantes de una carrera filtrados por ciudad.
     *
     * @param carrera nombre de carrera
     * @param ciudad ciudad de residencia
     */
    private static void listarEstudiantesPorCarreraYCiudad(String carrera, String ciudad) {
        System.out.println("\n=== 2g - Estudiantes por carrera y ciudad ===");
        EstudianteRepository repo = new EstudianteRepository();
        List<Estudiante> estudiantes = repo.buscarPorCarreraYCiudad(carrera, ciudad);

        System.out.println("Total: " + estudiantes.size() + " estudiantes de " + carrera + " en " + ciudad);
        for (Estudiante e : estudiantes) {
            System.out.println(e);
        }
    }

    /**
     * Genera y muestra el reporte anual de carreras.
     */
    private static void generarReporteCarreras() {
        System.out.println("\n=== 3 - Reporte anual de carreras ===");
        CarreraRepository repo = new CarreraRepository();
        List<CarreraReporteDTO> reporte = repo.generarReporte();

        String carreraActual = null;
        for (CarreraReporteDTO fila : reporte) {
            if (!fila.getCarrera().equals(carreraActual)) {
                carreraActual = fila.getCarrera();
                System.out.println("\nCarrera: " + carreraActual);
            }
            System.out.printf("  Año %d — Inscriptos: %d  |  Egresados: %d%n",
                    fila.getAnio(), fila.getInscriptos(), fila.getEgresados());
        }
    }

    /**
     * Pobla la base con los datos CSV.
     */
    private static void poblarDB() {
        DataLoader.inicializarMetadata();
    }
}
