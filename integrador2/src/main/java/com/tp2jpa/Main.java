package com.tp2jpa;

import com.tp2jpa.entities.Estudiante;

import com.tp2jpa.factory.JPAUtil;
import com.tp2jpa.repository.EstudianteRepository;

public class Main {

    public static void main(String[] args) {

        // a) Dar de alta a un Estudiante.
        EstudianteRepository estudianteRepo = new EstudianteRepository();

        Estudiante estudiante = new Estudiante(
                "Nahuel",       // nombres
                "Di Fiore",      // apellido
                32,           // edad
                'M',          // genero
                "37385519",   // numeroDocumento
                "Loberia",   // ciudadResidencia
                "1"       // numeroLibretaUniversitaria
        );

        estudianteRepo.guardar(estudiante);
        System.out.println("Estudiante guardado: " + estudiante);

        JPAUtil.close();
    }
}
