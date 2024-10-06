package co.edu.unicauca.sed.api.dto;

import lombok.Data;

@Data
public class EncuestaEstudianteDTO {
    // Datos para EvaluacionEstudiante (sin fecha)
    private String observacion;
    private String firma;

    // Datos para EncuestaEstudiante (con fecha)
    private Integer oidEncuesta;
    private String fechaEvaluacion; // formato ISO 8601 (yyyy-MM-dd'T'HH:mm:ss)
}