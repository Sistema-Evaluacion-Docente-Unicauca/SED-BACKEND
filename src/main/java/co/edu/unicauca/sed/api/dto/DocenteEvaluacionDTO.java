package co.edu.unicauca.sed.api.dto;

import lombok.Data;

/**
 * DTO para la información de los docentes activos y su progreso de evaluación.
 */
@Data
public class DocenteEvaluacionDTO {
    private Integer oidUsuario;
    private String nombreDocente;
    private String identificacion;
    private String contratacion;
    private Float porcentajeEvaluacionCompletado;
    private String estadoConsolidado;

    public DocenteEvaluacionDTO(Integer oidUsuario, String nombreDocente, String identificacion, String contratacion, float porcentajeEvaluacionCompletado, String estadoConsolidado) {
        this.oidUsuario = oidUsuario;
        this.nombreDocente = nombreDocente;
        this.identificacion = identificacion;
        this.contratacion = contratacion;
        this.porcentajeEvaluacionCompletado = porcentajeEvaluacionCompletado;
        this.estadoConsolidado = estadoConsolidado;
    }
}
