package co.edu.unicauca.sed.api.dto;

import java.time.LocalDateTime;

public class FuenteDTO {

    private Integer oidFuente;
    private String tipoFuente;
    private Float calificacion;
    private String nombreDocumentoFuente;
    private String nombreDocumentoInforme;
    private String observacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String estadoFuente;

    // Constructor
    public FuenteDTO(Integer oidFuente, String tipoFuente, Float calificacion, String nombreDocumentoFuente, String nombreDocumentoInforme, String observacion, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, String estadoFuente) {
        this.oidFuente = oidFuente;
        this.tipoFuente = tipoFuente;
        this.calificacion = calificacion;
        this.nombreDocumentoFuente = nombreDocumentoFuente;
        this.nombreDocumentoInforme = nombreDocumentoInforme;
        this.observacion = observacion;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.estadoFuente = estadoFuente;
    }

    public FuenteDTO ( Integer oidFuente, String estadoFuente, Float calificacion, String tipoFuente) {
        this.oidFuente = oidFuente;
        this.estadoFuente = estadoFuente;
        this.calificacion = calificacion;
        this.tipoFuente = tipoFuente;
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

    public String getNombreDocumentoFuente() {
        return nombreDocumentoFuente;
    }

    public void setNombreDocumentoFuente(String nombreDocumentoFuente) {
        this.nombreDocumentoFuente = nombreDocumentoFuente;
    }

    public String getNombreDocumentoInforme() {
        return nombreDocumentoInforme;
    }

    public void setNombreDocumento(String nombreDocumentoInforme) {
        this.nombreDocumentoInforme = nombreDocumentoInforme;
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

    public String getEstadoFuente() {
        return estadoFuente;
    }

    public void setEstadoFuente(String estadoFuente) {
        this.estadoFuente = estadoFuente;
    }
}
