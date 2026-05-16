package springboot.app.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import springboot.app.modelos.Perro;
import springboot.app.repositorios.PerroRepositorio;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CargaDeDatos {

    private final PerroRepositorio perroRepositorio;

    @Autowired
    public CargaDeDatos(PerroRepositorio perroRepositorio) {
        this.perroRepositorio = perroRepositorio;
    }

    public void cargarDatosDesdeCSV() throws IOException {
        File archivoCSV = ResourceUtils.getFile("src/main/java/springboot/app/csv/Perros.csv");

        try (FileReader reader = new FileReader(archivoCSV);
             CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {

            for (CSVRecord csvRecord : csvParser) {
                Perro perro = new Perro();
                perro.setNombre(csvRecord.get("nombre"));
                perro.setRaza(csvRecord.get("raza"));
                perro.setEdad(Integer.parseInt(csvRecord.get("edad")));
                perro.setHabilidad(csvRecord.get("habilidad"));
                perroRepositorio.save(perro); // Guarda el perro en la base de datos
            }
        }
    }

}

