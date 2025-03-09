package co.edu.unicauca.sed.api.dto.actividad;

import co.edu.unicauca.sed.api.domain.EstadoActividad;
import co.edu.unicauca.sed.api.domain.TipoActividad;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for representing activity details with evaluator information.
 */
public class ActividadDTOEvaluador {

    private Integer oidActividad;
    private String nombreActividad;
    private Float horas;
    private Float semanas;
    private EstadoActividad estadoActividad;
    private Boolean informeEjecutivo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private TipoActividad tipoActividad;
    private List<FuenteDTO> fuentes;
    private UsuarioDTO evaluado;

    /**
     * Constructor with all fields.
     *
     * @param oidActividad       ID of the activity.
     * @param nombreActividad    Name of the activity.
     * @param horas              Hours spent on the activity.
     * @param semanas            Weeks spent on the activity.
     * @param estadoActividad    Status of the activity.
     * @param informeEjecutivo   Whether an executive report is required.
     * @param fechaCreacion      Creation date of the activity.
     * @param fechaActualizacion Last update date of the activity.
     * @param tipoActividad      Type of the activity.
     * @param fuentes            List of associated sources.
     * @param evaluado           Evaluated user information.
     */
    public ActividadDTOEvaluador(
            Integer oidActividad,
            String nombreActividad,
            Float horas,
            Float semanas,
            EstadoActividad estadoActividad,
            Boolean informeEjecutivo,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaActualizacion,
            TipoActividad tipoActividad,
            List<FuenteDTO> fuentes,
            UsuarioDTO evaluado) {
        this.oidActividad = oidActividad;
        this.nombreActividad = nombreActividad;
        this.horas = horas;
        this.semanas = semanas;
        this.estadoActividad = estadoActividad;
        this.informeEjecutivo = informeEjecutivo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.tipoActividad = tipoActividad;
        this.fuentes = fuentes;
        this.evaluado = evaluado;
    }

    // Default constructor
    public ActividadDTOEvaluador() {
    }

    // Getters and setters

    public Integer getOidActividad() {
        return oidActividad;
    }

    public void setOidActividad(Integer oidActividad) {
        this.oidActividad = oidActividad;
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

    public EstadoActividad getEstadoActividad() {
        return estadoActividad;
    }

    public void setEstadoActividad(EstadoActividad estadoActividad) {
        this.estadoActividad = estadoActividad;
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
