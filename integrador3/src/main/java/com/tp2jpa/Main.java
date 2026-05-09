package com.tp2jpa;

import com.tp2jpa.utils.DataLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.IOException;

@SpringBootApplication
public class Main extends SpringBootServletInitializer {

    @Autowired
    private DataLoader dataLoader;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Main.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @PostConstruct
    public void init() throws IOException {
        dataLoader.cargarDatos();
    }
}
