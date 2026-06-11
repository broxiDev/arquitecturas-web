package com.microservices.msvc.estudiantes.utils.csv;

import com.microservices.msvc.estudiantes.entities.Estudiante;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    private static Reader abrirRecurso(String archivo) {
        return new InputStreamReader(
            CsvLoader.class.getClassLoader().getResourceAsStream(archivo)
        );
    }

    public static List<Estudiante> cargarEstudiantes() throws IOException {
        List<Estudiante> lista = new ArrayList<>();
        try (CSVParser p = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(abrirRecurso("estudiantes.csv"))) {
            for (CSVRecord r : p)
                lista.add(new Estudiante(
                    Long.parseLong(r.get("DNI")),
                    r.get("nombre"),
                    r.get("apellido"),
                    Integer.parseInt(r.get("edad")),
                    r.get("genero"),
                    r.get("ciudad"),
                    Long.parseLong(r.get("LU"))
                ));
        }
        return lista;
    }
}
