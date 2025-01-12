package co.edu.unicauca.sed.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import co.edu.unicauca.sed.api.model.TipoActividad;

public class ActividadDTO {

    private Integer oidActividad;
    private String codigoActividad;
    private String nombre;
    private Float horasTotales;
    private Boolean informeEjecutivo;
    private String codVRI;
    private String actoAdministrativo;  // Campo movido después de codVRI
    private Short estadoActividad;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private TipoActividad tipoActividad;
    private List<FuenteDTO> fuentes;
    private UsuarioDTO evaluador;

    // Constructor actualizado
    public ActividadDTO(Integer oidActividad, String codigoActividad, String nombre, Float horasTotales, Boolean informeEjecutivo, String codVRI, String actoAdministrativo, Short estadoActividad, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, TipoActividad tipoActividad, List<FuenteDTO> fuentes, UsuarioDTO evaluador) {
        this.oidActividad = oidActividad;
        this.codigoActividad = codigoActividad;
        this.nombre = nombre;
        this.horasTotales = horasTotales;
        this.informeEjecutivo = informeEjecutivo;
        this.codVRI = codVRI;
        this.actoAdministrativo = actoAdministrativo;
        this.estadoActividad = estadoActividad;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.tipoActividad = tipoActividad;
        this.fuentes = fuentes;
        this.evaluador = evaluador;
    }

    // Getters y Setters actualizados
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

    public Float getHorasTotales() {
        return horasTotales;
    }

    public void setHorasTotales(Float horasTotales) {
        this.horasTotales = horasTotales;
    }

    public Boolean getInformeEjecutivo() {
        return informeEjecutivo;
    }

    public void setInformeEjecutivo(Boolean informeEjecutivo) {
        this.informeEjecutivo = informeEjecutivo;
    }

    public String getCodVRI() {
        return codVRI;
    }

    public void setCodVRI(String codVRI) {
        this.codVRI = codVRI;
    }

    public Short getEstadoActividad() {
        return estadoActividad;
    }

    public void setEstadoActividad(Short estadoActividad) {
        this.estadoActividad = estadoActividad;
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

    public UsuarioDTO getEvaluador() {
        return evaluador;
    }

    public void setEvaluador(UsuarioDTO evaluador) {
        this.evaluador = evaluador;
    }

    public String getActoAdministrativo() {
        return actoAdministrativo;
    }

    public void setActoAdministrativo(String actoAdministrativo) {
        this.actoAdministrativo = actoAdministrativo;
    }
}
