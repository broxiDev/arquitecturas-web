package springboot.app.dtos;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Getter
public class PerroDTO {
    private long id;

    String nombre;

    String raza;

    String habilidad;

    int edad;

    public PerroDTO(String nombre, String raza, int edad, String habilidad) {
        this.nombre = nombre;
        this.raza = raza;
        this.habilidad = habilidad;
        this.edad = edad;
    }
}
