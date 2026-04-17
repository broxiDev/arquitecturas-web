package com.tp2jpa;

import com.tp2jpa.dto.CarreraInscriptosDTO;
import com.tp2jpa.dto.CarreraReporteDTO;
import com.tp2jpa.dto.EstudianteDTO;
import com.tp2jpa.entities.Carrera;
import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.entities.Estudiante.Genero;
import com.tp2jpa.factory.JPAUtil;
import com.tp2jpa.repository.CarreraRepository;
import com.tp2jpa.repository.EstudianteRepository;
import com.tp2jpa.repository.InscripcionRepository;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        CarreraRepository carreraRepo = new CarreraRepository();
        EstudianteRepository estudianteRepo = new EstudianteRepository();
        InscripcionRepository inscripcionRepo = new InscripcionRepository();

        // ============================================================
        // CARGA DE DATOS DE PRUEBA
        // ============================================================

        // Carreras
        Carrera sistemas = new Carrera("Sistemas");
        Carrera contaduria = new Carrera("Contador Publico");
        Carrera abogacia = new Carrera("Abogacia");
        carreraRepo.guardar(sistemas);
        carreraRepo.guardar(contaduria);
        carreraRepo.guardar(abogacia);

        // Estudiantes — 2a: dar de alta
        Estudiante juan = new Estudiante("Juan", "Perez", 22, Genero.MASCULINO, "11111111", "Tandil", "LU001");
        Estudiante ana = new Estudiante("Ana", "Garcia", 24, Genero.FEMENINO, "22222222", "Tandil", "LU002");
        Estudiante carlos = new Estudiante("Carlos", "Lopez", 21, Genero.MASCULINO, "33333333", "Buenos Aires", "LU003");
        Estudiante maria = new Estudiante("Maria", "Diaz", 23, Genero.FEMENINO, "44444444", "Buenos Aires", "LU004");
        Estudiante pedro = new Estudiante("Pedro", "Gomez", 25, Genero.MASCULINO, "55555555", "Mar del Plata", "LU005");
        estudianteRepo.guardar(juan);
        estudianteRepo.guardar(ana);
        estudianteRepo.guardar(carlos);
        estudianteRepo.guardar(maria);
        estudianteRepo.guardar(pedro);

        // Recargar carreras con id
        sistemas = carreraRepo.buscarPorNombre("Sistemas");
        contaduria = carreraRepo.buscarPorNombre("Contador Publico");
        abogacia = carreraRepo.buscarPorNombre("Abogacia");

        // Inscripciones — 2b: matricular
        inscripcionRepo.matricular(juan, sistemas, 3, false);
        inscripcionRepo.matricular(ana, sistemas, 5, true);
        inscripcionRepo.matricular(ana, contaduria, 2, false);
        inscripcionRepo.matricular(carlos, abogacia, 4, false);
        inscripcionRepo.matricular(maria, sistemas, 5, true);
        inscripcionRepo.matricular(pedro, sistemas, 1, false);
        inscripcionRepo.matricular(pedro, abogacia, 2, false);


    }

    private static void separador(String titulo) {
        System.out.println("\n========================================");
        System.out.println(">>> " + titulo);
        System.out.println("========================================");
    }
}

