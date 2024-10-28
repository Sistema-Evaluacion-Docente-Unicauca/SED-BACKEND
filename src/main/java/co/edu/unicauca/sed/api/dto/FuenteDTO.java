package co.edu.unicauca.sed.api.dto;

import java.time.LocalDateTime;

public class FuenteDTO {

    private Integer oidFuente;
    private String tipoFuente;
    private Float calificacion;
    private String nombreDocumento;
    private String observacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String estadoFuente;

    // Constructor
    public FuenteDTO(Integer oidFuente, String tipoFuente, Float calificacion, String nombreDocumento, String observacion,
                    LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, String estadoFuente) {
        this.oidFuente = oidFuente;
        this.tipoFuente = tipoFuente;
        this.calificacion = calificacion;
        this.nombreDocumento = nombreDocumento;
        this.observacion = observacion;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.estadoFuente = estadoFuente;
    }

    // Getters and Setters
    public Integer getOidFuente() {
        return oidFuente;
    }

    public void setOidFuente(Integer oidFuente) {
        this.oidFuente = oidFuente;
    }

    public String getTipoFuente() {
        return tipoFuente;
    }

    public void setTipoFuente(String tipoFuente) {
        this.tipoFuente = tipoFuente;
    }

    public Float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Float calificacion) {
        this.calificacion = calificacion;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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

    public String getestadoFuente() {
        return estadoFuente;
    }

    public void setestadoFuente(String estadoFuente) {
        this.estadoFuente = estadoFuente;
    }
}
