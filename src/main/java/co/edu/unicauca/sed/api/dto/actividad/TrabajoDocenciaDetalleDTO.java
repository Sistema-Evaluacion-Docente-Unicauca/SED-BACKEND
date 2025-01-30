package co.edu.unicauca.sed.api.dto.actividad;

import java.time.LocalDateTime;
import java.util.List;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;
import co.edu.unicauca.sed.api.model.TipoActividad;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrabajoDocenciaDetalleDTO extends ActividadBaseDTO {

    private Integer oidTrabajoDocenciaDetalle;
    private String actoAdministrativo;

    // Constructor completo con herencia
    public TrabajoDocenciaDetalleDTO(Integer oidActividad, TipoActividad tipoActividad, Integer oidProceso,
            Integer oidEstadoActividad, String nombreActividad, Float horas, Float semanas,
            Boolean informeEjecutivo, LocalDateTime fechaCreacion,
            LocalDateTime fechaActualizacion, List<FuenteDTO> fuentes,
            Integer oidTrabajoDocenciaDetalle, String actoAdministrativo, UsuarioDTO evaluador, Integer oidEvaluado, Integer oidEvaluador) {
        super(oidActividad, tipoActividad, oidProceso, oidEstadoActividad, nombreActividad, horas, semanas,
                informeEjecutivo, fechaCreacion, fechaActualizacion, fuentes, null, evaluador, oidEvaluado, oidEvaluador);
        this.oidTrabajoDocenciaDetalle = oidTrabajoDocenciaDetalle;
        this.actoAdministrativo = actoAdministrativo;
    }

    public TrabajoDocenciaDetalleDTO(String actoAdministratito) {
        this.actoAdministrativo = actoAdministratito;
    }

    // Constructor vacío
    public TrabajoDocenciaDetalleDTO() {
        super();
    }

    // Getters y Setters
    public Integer getOidTrabajoDocenciaDetalle() {
        return oidTrabajoDocenciaDetalle;
    }

    public void setOidTrabajoDocenciaDetalle(Integer oidTrabajoDocenciaDetalle) {
        this.oidTrabajoDocenciaDetalle = oidTrabajoDocenciaDetalle;
    }

    public String getActoAdministrativo() {
        return actoAdministrativo;
    }

    public void setActoAdministrativo(String actoAdministrativo) {
        this.actoAdministrativo = actoAdministrativo;
    }
}
