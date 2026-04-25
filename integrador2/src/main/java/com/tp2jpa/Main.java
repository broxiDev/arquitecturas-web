package com.tp2jpa;

import com.tp2jpa.entities.Estudiante;
import com.tp2jpa.factory.JPAUtil;
import com.tp2jpa.repository.EstudianteRepository;
import com.tp2jpa.utils.DataLoader;

public class Main {

    public static void main(String[] args) {

        poblarDB(); // comentar luego de poblar la DB



        JPAUtil.close();
    }

    private static void poblarDB() {
        DataLoader.inicializarMetadata();
    }
}
