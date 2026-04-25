package micro.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DireccionDTO {

    private String ciudad;
    private String calle;
    private int numero;
    private String codigoPostal;

}
