package com.microservices.msvc.carreras;

import com.microservices.msvc.carreras.utils.DataLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.IOException;

@SpringBootApplication
@EnableFeignClients
public class MsvcCarrerasApplication {

    @Autowired
    private DataLoader dataLoader;

    public static void main(String[] args) {
        SpringApplication.run(MsvcCarrerasApplication.class, args);
    }

    @PostConstruct
    public void init() throws IOException {
        dataLoader.cargarDatos();
    }
}
