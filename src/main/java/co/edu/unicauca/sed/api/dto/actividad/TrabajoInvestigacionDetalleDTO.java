package co.edu.unicauca.sed.api.dto.actividad;

import java.time.LocalDateTime;
import java.util.List;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;
import co.edu.unicauca.sed.api.model.TipoActividad;

public class TrabajoInvestigacionDetalleDTO extends ActividadBaseDTO {

    private Integer oidTrabajoInvestigacionDetalle;
    private String actoAdministrativo;

    // Constructor completo con herencia
    public TrabajoInvestigacionDetalleDTO(Integer oidActividad, TipoActividad tipoActividad, Integer oidProceso,
            Integer oidEstadoActividad, String nombreActividad, Float horas, Float semanas,
            Boolean informeEjecutivo, LocalDateTime fechaCreacion,
            LocalDateTime fechaActualizacion, List<FuenteDTO> fuentes,
            Integer oidTrabajoInvestigacionDetalle, String actoAdministrativo, UsuarioDTO evaluador,
            Integer oidEvaluado, Integer oidEvaluador) {
        super(oidActividad, tipoActividad, oidProceso, oidEstadoActividad, nombreActividad, horas, semanas,
                informeEjecutivo, fechaCreacion, fechaActualizacion, fuentes, null, evaluador, oidEvaluado, oidEvaluador);
        this.oidTrabajoInvestigacionDetalle = oidTrabajoInvestigacionDetalle;
        this.actoAdministrativo = actoAdministrativo;
    }

    public TrabajoInvestigacionDetalleDTO(String actoAdministrativo) {
        this.actoAdministrativo = actoAdministrativo;
    }

    // Constructor vacío
    public TrabajoInvestigacionDetalleDTO() {
        super();
    }

    // Getters y Setters
    public Integer getOidTrabajoInvestigacionDetalle() {
        return oidTrabajoInvestigacionDetalle;
    }

    public void setOidTrabajoInvestigacionDetalle(Integer oidTrabajoInvestigacionDetalle) {
        this.oidTrabajoInvestigacionDetalle = oidTrabajoInvestigacionDetalle;
    }

    public String getActoAdministrativo() {
        return actoAdministrativo;
    }

    public void setActoAdministrativo(String actoAdministrativo) {
        this.actoAdministrativo = actoAdministrativo;
    }
}
