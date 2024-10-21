package co.edu.unicauca.sed.api.dto;

public class FuenteDTO {

    private Integer oidFuente;
    private String tipoFuente;
    private Float calificacion;
    private String nombreDocumento;
    private String observacion;
    private String fechaCreacion;
    private String fechaActualizacion;
    private String estadoFuente;

    // Constructor
    public FuenteDTO(Integer oidFuente, String tipoFuente, Float calificacion, String nombreDocumento, String observacion,
                     String fechaCreacion, String fechaActualizacion, String estadoFuente) {
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

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getestadoFuente() {
        return estadoFuente;
    }

    public void setestadoFuente(String estadoFuente) {
        this.estadoFuente = estadoFuente;
    }
}
