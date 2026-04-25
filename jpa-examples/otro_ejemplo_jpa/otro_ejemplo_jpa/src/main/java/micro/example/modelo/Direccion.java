package micro.example.modelo;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "direccion")
@Data
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String ciudad;
    private String calle;
    private int numero;
    private String codigoPostal;

    @OneToMany(mappedBy = "direccion")
    private List<Persona> personas;

    // getters y setters
}

