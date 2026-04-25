package micro.example.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonaDTO {
    private String nombre;
    private int edad;
    private String ciudad;
    private String calle;
    private int numero;
}
