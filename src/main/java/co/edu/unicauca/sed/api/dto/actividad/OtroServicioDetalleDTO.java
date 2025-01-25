package co.edu.unicauca.sed.api.dto.actividad;

import java.time.LocalDateTime;
import java.util.List;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;
import co.edu.unicauca.sed.api.model.TipoActividad;

public class OtroServicioDetalleDTO extends ActividadBaseDTO {

    private Integer oidOtroServicioDetalle;
    private String actoAdministrativo;
    private String detalle;

    // Constructor completo con herencia
    public OtroServicioDetalleDTO(Integer oidActividad, TipoActividad tipoActividad, Integer oidProceso,
            Integer oidEstadoActividad, String nombreActividad, Float horas, Float semanas,
            Boolean informeEjecutivo, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
            List<FuenteDTO> fuentes, Integer oidOtroServicioDetalle, String actoAdministrativo,
            String detalle, UsuarioDTO evaluador, Integer oidEvaluado, Integer oidEvaluador) {
        super(oidActividad, tipoActividad, oidProceso, oidEstadoActividad, nombreActividad, horas, semanas,
                informeEjecutivo, fechaCreacion, fechaActualizacion, fuentes, null, evaluador, oidEvaluado, oidEvaluador);
        this.oidOtroServicioDetalle = oidOtroServicioDetalle;
        this.actoAdministrativo = actoAdministrativo;
        this.detalle = detalle;
    }

    public OtroServicioDetalleDTO(String actoAdministrativo, String detalle) {
        this.actoAdministrativo = actoAdministrativo;
        this.detalle = detalle;
    }

    // Constructor vacío
    public OtroServicioDetalleDTO() {
        super();
    }

    // Getters y Setters
    public Integer getOidOtroServicioDetalle() {
        return oidOtroServicioDetalle;
    }

    public void setOidOtroServicioDetalle(Integer oidOtroServicioDetalle) {
        this.oidOtroServicioDetalle = oidOtroServicioDetalle;
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
