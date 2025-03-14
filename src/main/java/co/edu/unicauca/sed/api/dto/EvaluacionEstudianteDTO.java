package co.edu.unicauca.sed.api.dto;

import lombok.Data;

/**
 * DTO para manejar la evaluación de un estudiante en la encuesta.
 */
@Data
public class EvaluacionEstudianteDTO {
    private Integer oidFuente;
    private Float calificacion;
    private String observacion;
    private String firma;
}
