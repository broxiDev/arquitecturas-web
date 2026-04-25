package micro.example.modelo;



import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "edad")
    private int edad;

    @Column(name = "e-mail",unique = true)
    private String mail;

    @ManyToOne
    @JoinColumn(name = "direccion_id")
    private Direccion direccion;

    // getters y setters
}

