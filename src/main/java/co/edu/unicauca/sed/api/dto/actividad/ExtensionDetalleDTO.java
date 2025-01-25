package co.edu.unicauca.sed.api.dto.actividad;

import java.time.LocalDateTime;
import java.util.List;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;
import co.edu.unicauca.sed.api.model.TipoActividad;

public class ExtensionDetalleDTO extends ActividadBaseDTO {

    private Integer oidExtensionDetalle;
    private String actoAdministrativo;
    private String nombreProyecto;

    // Constructor completo con herencia
    public ExtensionDetalleDTO(Integer oidActividad, TipoActividad tipoActividad, Integer oidProceso,
            Integer oidEstadoActividad, String nombreActividad, Float horas, Float semanas,
            Boolean informeEjecutivo, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
            List<FuenteDTO> fuentes, Integer oidExtensionDetalle, String actoAdministrativo,
            String nombreProyecto, UsuarioDTO evaluador, Integer oidEvaluado, int oidEvaluador) {
        super(oidActividad, tipoActividad, oidProceso, oidEstadoActividad, nombreActividad, horas, semanas,
                informeEjecutivo, fechaCreacion, fechaActualizacion, fuentes, null, evaluador, oidEvaluado, oidEvaluador);
        this.oidExtensionDetalle = oidExtensionDetalle;
        this.actoAdministrativo = actoAdministrativo;
        this.nombreProyecto = nombreProyecto;
    }

    public ExtensionDetalleDTO(String actoAdministrativo, String nombreProyecto) {
        this.actoAdministrativo = actoAdministrativo;
        this.nombreProyecto = nombreProyecto;
    }

    // Constructor vacío
    public ExtensionDetalleDTO() {
        super();
    }

    // Getters y Setters
    public Integer getOidExtensionDetalle() {
        return oidExtensionDetalle;
    }

    public void setOidExtensionDetalle(Integer oidExtensionDetalle) {
        this.oidExtensionDetalle = oidExtensionDetalle;
    }

    public String getActoAdministrativo() {
        return actoAdministrativo;
    }

    public void setActoAdministrativo(String actoAdministrativo) {
        this.actoAdministrativo = actoAdministrativo;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }
}
