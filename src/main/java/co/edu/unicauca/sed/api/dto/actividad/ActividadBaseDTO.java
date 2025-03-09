package co.edu.unicauca.sed.api.dto.actividad;

import java.time.LocalDateTime;
import java.util.List;

import co.edu.unicauca.sed.api.domain.TipoActividad;
import co.edu.unicauca.sed.api.dto.AtributoDTO;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;

public class ActividadBaseDTO {

    private Integer oidActividad;
    private TipoActividad tipoActividad;
    private Integer oidProceso;
    private Integer oidEstadoActividad;
    private String nombreActividad;
    private Float horas;
    private Float semanas;
    private Boolean informeEjecutivo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private List<FuenteDTO> fuentes;
    private List<AtributoDTO> atributos;
    private UsuarioDTO evaluador;
    private Integer oidEvaluado;
    private Integer oidEvaluador;

    // Constructor completo
    public ActividadBaseDTO(Integer oidActividad, TipoActividad tipoActividad, Integer oidProceso, Integer oidEstadoActividad,
                             String nombreActividad, Float horas, Float semanas, Boolean informeEjecutivo,
                             LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, List<FuenteDTO> fuentes,
                             List<AtributoDTO> atributos, UsuarioDTO evaluador, Integer oidEvaluado, Integer oidEvaluador) {
        this.oidActividad = oidActividad;
        this.tipoActividad = tipoActividad;
        this.oidProceso = oidProceso;
        this.oidEstadoActividad = oidEstadoActividad;
        this.nombreActividad = nombreActividad;
        this.horas = horas;
        this.semanas = semanas;
        this.informeEjecutivo = informeEjecutivo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.fuentes = fuentes;
        this.atributos = atributos;
        this.evaluador = evaluador;
        this.oidEvaluado = oidEvaluado;
        this.oidEvaluador = oidEvaluador;
    }

    // Constructor vacío
    public ActividadBaseDTO() {}

    // Getters y Setters
    public Integer getOidActividad() {
        return oidActividad;
    }

    public void setOidActividad(Integer oidActividad) {
        this.oidActividad = oidActividad;
    }

    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(TipoActividad tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public Integer getOidProceso() {
        return oidProceso;
    }

    public void setOidProceso(Integer oidProceso) {
        this.oidProceso = oidProceso;
    }

    public Integer getOidEstadoActividad() {
        return oidEstadoActividad;
    }

    public void setOidEstadoActividad(Integer oidEstadoActividad) {
        this.oidEstadoActividad = oidEstadoActividad;
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public Float getHoras() {
        return horas;
    }

    public void setHoras(Float horas) {
        this.horas = horas;
    }

    public Float getSemanas() {
        return semanas;
    }

    public void setSemanas(Float semanas) {
        this.semanas = semanas;
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

    public List<FuenteDTO> getFuentes() {
        return fuentes;
    }

    public void setFuentes(List<FuenteDTO> fuentes) {
        this.fuentes = fuentes;
    }

    public List<AtributoDTO> getAtributos() {
        return atributos;
    }

    public void setAtributos(List<AtributoDTO> atributos) {
        this.atributos = atributos;
    }

    public UsuarioDTO getEvaluador() {
        return evaluador;
    }

    public void setEvaluador(UsuarioDTO evaluador) {
        this.evaluador = evaluador;
    }

    public Integer getOidEvaluado() {
        return oidEvaluado;
    }

    public void setOidEvaluado(Integer oidEvaluado) {
        this.oidEvaluado = oidEvaluado;
    }

    public Integer getOidEvaluador() {
        return oidEvaluador;
    }

    public void setOidEvaluador(Integer oidEvaluador) {
        this.oidEvaluador = oidEvaluador;
    }
}
