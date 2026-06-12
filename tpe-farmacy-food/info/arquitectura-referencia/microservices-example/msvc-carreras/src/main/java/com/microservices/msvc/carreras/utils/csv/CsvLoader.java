package com.microservices.msvc.carreras.utils.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    public record CarreraRaw(long csvId, String nombre, int duracion) {}

    public record EstudianteCarreraRaw(long dniEstudiante, long idCarreraCsv,
                                       int inscripcion, int graduacion, int antiguedad) {}

    private static Reader abrirRecurso(String archivo) {
        return new InputStreamReader(
            CsvLoader.class.getClassLoader().getResourceAsStream(archivo)
        );
    }

    public static List<CarreraRaw> cargarCarreras() throws IOException {
        List<CarreraRaw> lista = new ArrayList<>();
        try (CSVParser p = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(abrirRecurso("carreras.csv"))) {
            for (CSVRecord r : p)
                lista.add(new CarreraRaw(
                    Long.parseLong(r.get("id_carrera")),
                    r.get("carrera"),
                    Integer.parseInt(r.get("duracion"))
                ));
        }
        return lista;
    }

    public static List<EstudianteCarreraRaw> cargarEstudianteCarreras() throws IOException {
        List<EstudianteCarreraRaw> lista = new ArrayList<>();
        try (CSVParser p = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(abrirRecurso("estudianteCarrera.csv"))) {
            for (CSVRecord r : p)
                lista.add(new EstudianteCarreraRaw(
                    Long.parseLong(r.get("id_estudiante")),
                    Long.parseLong(r.get("id_carrera")),
                    Integer.parseInt(r.get("inscripcion")),
                    Integer.parseInt(r.get("graduacion")),
                    Integer.parseInt(r.get("antiguedad"))
                ));
        }
        return lista;
    }
}
