package co.edu.unicauca.sed.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import co.edu.unicauca.sed.api.model.TipoActividad;

// DTO for activity details
public class ActividadDTO {

    private String codigoActividad; // Activity code
    private String nombre; // Activity name
    private String horas; // Hours assigned to the activity
    private LocalDateTime fechaCreacion; // Creation date of the activity
    private LocalDateTime fechaActualizacion; // Last update date of the activity
    private TipoActividad tipoActividad; // Type of activity
    private List<FuenteDTO> fuentes; // List of associated sources
    private UsuarioDTO evaluador; // Evaluator information (restricted)

    // Constructor with all necessary fields
    public ActividadDTO(String codigoActividad, String nombre, String horas,
                        LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                        TipoActividad tipoActividad, List<FuenteDTO> fuentes, UsuarioDTO evaluador) {
        this.codigoActividad = codigoActividad;
        this.nombre = nombre;
        this.horas = horas;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.tipoActividad = tipoActividad;
        this.fuentes = fuentes;
        this.evaluador = evaluador;
    }

    // Getters and setters
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

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
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
}
