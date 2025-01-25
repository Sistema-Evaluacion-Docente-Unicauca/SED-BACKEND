package co.edu.unicauca.sed.api.dto.actividad;

import java.time.LocalDateTime;

public class ProyectoInvestigacionDetalleDTO {

    private Integer oidProyectoInvestigacionDetalle;
    private String vri;
    private String nombreProyecto;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructor completo
    public ProyectoInvestigacionDetalleDTO(Integer oidProyectoInvestigacionDetalle, String vri,
                                           String nombreProyecto, LocalDateTime fechaCreacion,
                                           LocalDateTime fechaActualizacion) {
        this.oidProyectoInvestigacionDetalle = oidProyectoInvestigacionDetalle;
        this.vri = vri;
        this.nombreProyecto = nombreProyecto;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public ProyectoInvestigacionDetalleDTO(String vri, String nombreProyecto) {
        this.vri = vri;
        this.nombreProyecto = nombreProyecto;
    }

    // Constructor vacío
    public ProyectoInvestigacionDetalleDTO() {}

    // Getters y Setters
    public Integer getOidProyectoInvestigacionDetalle() {
        return oidProyectoInvestigacionDetalle;
    }

    public void setOidProyectoInvestigacionDetalle(Integer oidProyectoInvestigacionDetalle) {
        this.oidProyectoInvestigacionDetalle = oidProyectoInvestigacionDetalle;
    }

    public String getVri() {
        return vri;
    }

    public void setVri(String vri) {
        this.vri = vri;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
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
