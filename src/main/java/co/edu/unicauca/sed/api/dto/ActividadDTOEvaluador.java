package co.edu.unicauca.sed.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import co.edu.unicauca.sed.api.model.TipoActividad;

// DTO for activity details
public class ActividadDTOEvaluador {

    private Integer oidActividad;
    private String codigoActividad;
    private String nombre;
    private Float horas;
    private Boolean informeEjecutivo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private TipoActividad tipoActividad;
    private List<FuenteDTO> fuentes;
    private UsuarioDTO evaluado;

    // Constructor with all necessary fields
    public ActividadDTOEvaluador(Integer oidActividad, String codigoActividad, String nombre, Float horas, Boolean informeEjecutivo, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                                TipoActividad tipoActividad, List<FuenteDTO> fuentes, UsuarioDTO evaluado) {
        this.oidActividad = oidActividad;
        this.codigoActividad = codigoActividad;
        this.nombre = nombre;
        this.horas = horas;
        this.informeEjecutivo = informeEjecutivo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.tipoActividad = tipoActividad;
        this.fuentes = fuentes;
        this.evaluado = evaluado;
    }

    // Getters and setters
    public Integer getOidActividad() {
        return oidActividad;
    }

    public void setOidActividad(Integer oidActividad) {
        this.oidActividad = oidActividad;
    }

    public String getCodigoActividad() {
        return codigoActividad;
    }

    public void setCodigoActividad(String codigoActividad) {
        this.codigoActividad = codigoActividad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Float getHoras() {
        return horas;
    }

    public void setHoras(Float horas) {
        this.horas = horas;
    }

    public Boolean getInformeEjecutivo() {
        return informeEjecutivo;
    }

    public void setInformeEjecutivo(Boolean informeEjecutivo) {
        this.informeEjecutivo = informeEjecutivo;
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

    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(TipoActividad tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public List<FuenteDTO> getFuentes() {
        return fuentes;
    }

    public void setFuentes(List<FuenteDTO> fuentes) {
        this.fuentes = fuentes;
    }

    public UsuarioDTO getEvaluado() {
        return evaluado;
    }

    public void setEvaluado(UsuarioDTO evaluado) {
        this.evaluado = evaluado;
    }
}
