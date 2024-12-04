package co.edu.unicauca.sed.api.dto;

import lombok.Data;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * DTO para consolidar información.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ConsolidadoDTO {
    private String nombreDocente;
    private String numeroIdentificacion;
    private String periodoAcademico;
    private String facultad;
    private String departamento;
    private String categoria;
    private String tipoContratacion;
    private String dedicacion;
    private Map<String, List<Map<String, Object>>> actividades;
    private Float totalHorasSemanales;
    private Float totalPorcentaje;
    private Double totalAcumulado;
}
