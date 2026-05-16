package springboot.app.modelos;

import jakarta.persistence.*;

import java.util.Optional;

@Entity
@Table(name = "perros")
public class Perro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String nombre;

    String raza;

    String habilidad;

    int edad;


    public Perro() {

    }

    public Perro( String nombre, String raza, int edad,String habilidad) {
        this.nombre = nombre;
        this.raza  = raza;
        this.edad = edad;
        this.habilidad = habilidad;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public void setHabilidad(String habilidad) {
        this.habilidad = habilidad;
    }

    @Override
    public String toString() {
        return "Perro{" +
                "nombre='" + nombre + '\'' +
                ", raza='" + raza + '\'' +
                ", habilidad='" + habilidad + '\'' +
                ", edad=" + edad +
                '}';
    }


}
