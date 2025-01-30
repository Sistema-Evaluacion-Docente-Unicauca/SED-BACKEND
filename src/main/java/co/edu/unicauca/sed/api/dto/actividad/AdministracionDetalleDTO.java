package co.edu.unicauca.sed.api.dto.actividad;

import java.time.LocalDateTime;
import java.util.List;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;
import co.edu.unicauca.sed.api.model.TipoActividad;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdministracionDetalleDTO extends ActividadBaseDTO {

    private Integer oidAdministracionDetalle;
    private String actoAdministrativo;
    private String detalle;

    // Constructor completo con herencia
    public AdministracionDetalleDTO(Integer oidActividad, TipoActividad tipoActividad, Integer oidProceso,
            Integer oidEstadoActividad, String nombreActividad, Float horas, Float semanas,
            Boolean informeEjecutivo, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
            List<FuenteDTO> fuentes, Integer oidAdministracionDetalle, String actoAdministrativo,
            String detalle, UsuarioDTO evaluador, Integer oidEvaluado, Integer oidEvaluador) {
        super(oidActividad, tipoActividad, oidProceso, oidEstadoActividad, nombreActividad, horas, semanas,
                informeEjecutivo, fechaCreacion, fechaActualizacion, fuentes, null, evaluador, oidEvaluado, oidEvaluador);
        this.oidAdministracionDetalle = oidAdministracionDetalle;
        this.actoAdministrativo = actoAdministrativo;
        this.detalle = detalle;
    }

    public AdministracionDetalleDTO(String actoAdministrativo, String detalle) {
        this.actoAdministrativo = actoAdministrativo;
        this.detalle = detalle;
    }

    // Constructor vacío
    public AdministracionDetalleDTO() {
        super();
    }

    // Getters y Setters
    public Integer getOidAdministracionDetalle() {
        return oidAdministracionDetalle;
    }

    public void setOidAdministracionDetalle(Integer oidAdministracionDetalle) {
        this.oidAdministracionDetalle = oidAdministracionDetalle;
    }

    public String getActoAdministrativo() {
        return actoAdministrativo;
    }

    public void setActoAdministrativo(String actoAdministrativo) {
        this.actoAdministrativo = actoAdministrativo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
