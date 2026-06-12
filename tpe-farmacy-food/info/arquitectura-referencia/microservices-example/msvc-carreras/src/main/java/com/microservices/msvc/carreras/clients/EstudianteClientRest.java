package com.microservices.msvc.carreras.clients;

import com.microservices.msvc.carreras.models.Estudiante;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "msvc-estudiantes", url = "http://localhost:8001")
public interface EstudianteClientRest {

    @GetMapping("/api/estudiantes/{dni}")
    Estudiante getEstudiante(@PathVariable Long dni);

    @GetMapping("/api/estudiantes/bulk")
    List<Estudiante> getEstudiantesBulk(@RequestParam("dnis") List<Long> dnis);
}
