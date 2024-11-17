package co.edu.unicauca.sed.api.dto;

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

    // Getters y Setters
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

    public Integer getOidActividad() {
        return oidActividad;
    }

    public void setOidActividad(Integer oidActividad) {
        this.oidActividad = oidActividad;
    }

    public String getInformeEjecutivo() {
        return informeEjecutivo;
    }

    public void setInformeEjecutivo(String informeEjecutivo) {
        this.informeEjecutivo = informeEjecutivo;
    }
}
