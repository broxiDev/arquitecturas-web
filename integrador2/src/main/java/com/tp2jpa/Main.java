package com.tp2jpa;

import com.tp2jpa.entities.Estudiante;

import com.tp2jpa.factory.JPAUtil;
import com.tp2jpa.repository.EstudianteRepository;

public class Main {

    public static void main(String[] args) {

        // a) Dar de alta a un Estudiante.
        EstudianteRepository estudianteRepo = new EstudianteRepository();

        Estudiante estudiante = new Estudiante(
                37385519L,   // dni
                "Nahuel",    // nombre
                "Di Fiore",  // apellido
                32,          // edad
                "Masculino", // genero
                "Loberia",   // ciudad
                1L           // lu
        );

        estudianteRepo.guardar(estudiante);
        System.out.println("Estudiante guardado: " + estudiante);

        JPAUtil.close();
    }
}
