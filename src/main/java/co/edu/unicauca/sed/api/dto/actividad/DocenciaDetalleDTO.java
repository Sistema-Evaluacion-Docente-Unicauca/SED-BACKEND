package co.edu.unicauca.sed.api.dto.actividad;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocenciaDetalleDTO {

    private Integer oidDocenciaDetalle;
    private String codigo;
    private String grupo;
    private String materia;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructor completo
    public DocenciaDetalleDTO(Integer oidDocenciaDetalle, String codigo, String grupo, String materia,
                              LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.oidDocenciaDetalle = oidDocenciaDetalle;
        this.codigo = codigo;
        this.grupo = grupo;
        this.materia = materia;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public DocenciaDetalleDTO(String codigo, String grupo, String materia) {
        this.codigo = codigo;
        this.grupo = grupo;
        this.materia = materia;
    }

    // Constructor vacío
    public DocenciaDetalleDTO() {}

    // Getters y Setters
    public Integer getOidDocenciaDetalle() {
        return oidDocenciaDetalle;
    }

    public void setOidDocenciaDetalle(Integer oidDocenciaDetalle) {
        this.oidDocenciaDetalle = oidDocenciaDetalle;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
