package co.edu.unicauca.sed.api.dto;

import lombok.Data;
@Data
public class FuenteCreateDTO {
    private String tipoFuente;
    private Float calificacion;
    private Integer oidActividad;
    private String informeEjecutivo;

    // Constructor vacío
    public FuenteCreateDTO() {}

    // Constructor con parámetros
    public FuenteCreateDTO(String tipoFuente, Float calificacion, Integer oidActividad, String informeEjecutivo) {
        this.tipoFuente = tipoFuente;
        this.calificacion = calificacion;
        this.oidActividad = oidActividad;
        this.informeEjecutivo = informeEjecutivo;
    }
}
