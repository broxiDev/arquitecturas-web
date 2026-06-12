package com.microservices.msvc.estudiantes;

import com.microservices.msvc.estudiantes.utils.DataLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MsvcEstudiantesApplication {

    @Autowired
    private DataLoader dataLoader;

    public static void main(String[] args) {
        SpringApplication.run(MsvcEstudiantesApplication.class, args);
    }

    @PostConstruct
    public void init() throws IOException {
        dataLoader.cargarDatos();
    }
}
