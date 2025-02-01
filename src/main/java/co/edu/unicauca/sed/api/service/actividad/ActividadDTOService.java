package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.RolDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;
import co.edu.unicauca.sed.api.dto.actividad.*;
import co.edu.unicauca.sed.api.model.*;
import co.edu.unicauca.sed.api.repository.*;
import co.edu.unicauca.sed.api.service.FuenteDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la conversión entre entidades Actividad y sus respectivos DTOs.
 */
@Service
public class ActividadDTOService {

    private static final String DEFAULT_NAME = "N/A";

    @Autowired
    private FuenteDTOService fuenteDTOService;

    @Autowired
    private AdministracionDetalleRepository administracionDetalleRepository;

    @Autowired
    private DocenciaDetalleRepository docenciaDetalleRepository;

    @Autowired
    private TrabajoDocenciaDetalleRepository trabajoDocenciaDetalleRepository;

    @Autowired
    private ProyectoInvestigacionDetalleRepository proyectoInvestigacionDetalleRepository;

    @Autowired
    private CapacitacionDetalleRepository capacitacionDetalleRepository;

    @Autowired
    private OtroServicioDetalleRepository otroServicioDetalleRepository;

    @Autowired
    private ExtensionDetalleRepository extensionDetalleRepository;

    @Autowired
    private TrabajoInvestigacionDetalleRepository trabajoInvestigacionDetalleRepository;

    /**
     * Convierte una entidad Actividad con AdministracionDetalle a su DTO.
     */
    public ActividadBaseDTO convertToAdministracionDetalleDTO(Actividad actividad) {
        AdministracionDetalle detalle = administracionDetalleRepository
                .findByActividadOidActividad(actividad.getOidActividad())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró un detalle de administración para la actividad con ID: "
                                + actividad.getOidActividad()));
        return buildActividadBaseDTO(actividad, detalle);
    }

    /**
     * Convierte una entidad Actividad con DocenciaDetalle a su DTO.
     */
    public ActividadBaseDTO convertToDocenciaDetalleDTO(Actividad actividad) {
        DocenciaDetalle detalle = docenciaDetalleRepository
                .findByActividadOidActividad(actividad.getOidActividad())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró un detalle de docencia para la actividad con ID: "
                                + actividad.getOidActividad()));
        return buildActividadBaseDTO(actividad, detalle);
    }

    /**
     * Convierte una entidad Actividad con TrabajoDocenciaDetalle a su DTO.
     */
    public ActividadBaseDTO convertToTrabajoDocenciaDetalleDTO(Actividad actividad) {
        TrabajoDocenciaDetalle detalle = trabajoDocenciaDetalleRepository
                .findByActividadOidActividad(actividad.getOidActividad())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró un detalle de trabajo de docencia para la actividad con ID: "
                                + actividad.getOidActividad()));
        return buildActividadBaseDTO(actividad, detalle);
    }

    /**
     * Convierte una entidad Actividad con ProyectoInvestigacionDetalle a su DTO.
     */
    public ActividadBaseDTO convertToProyectoInvestigacionDetalleDTO(Actividad actividad) {
        ProyectoInvestigacionDetalle detalle = proyectoInvestigacionDetalleRepository
                .findByActividadOidActividad(actividad.getOidActividad())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró un detalle de proyecto de investigación para la actividad con ID: "
                                + actividad.getOidActividad()));
        return buildActividadBaseDTO(actividad, detalle);
    }

    /**
     * Convierte una entidad Actividad con CapacitacionDetalle a su DTO.
     */
    public ActividadBaseDTO convertToCapacitacionDetalleDTO(Actividad actividad) {
        CapacitacionDetalle detalle = capacitacionDetalleRepository
                .findByActividadOidActividad(actividad.getOidActividad())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró un detalle de capacitación para la actividad con ID: "
                                + actividad.getOidActividad()));
        return buildActividadBaseDTO(actividad, detalle);
    }

    /**
     * Convierte una entidad Actividad con OtroServicioDetalle a su DTO.
     */
    public ActividadBaseDTO convertToOtroServicioDetalleDTO(Actividad actividad) {
        OtroServicioDetalle detalle = otroServicioDetalleRepository
                .findByActividadOidActividad(actividad.getOidActividad())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró un detalle de otro servicio para la actividad con ID: "
                                + actividad.getOidActividad()));
        return buildActividadBaseDTO(actividad, detalle);
    }

    /**
     * Convierte una entidad Actividad con ExtensionDetalle a su DTO.
     */
    public ActividadBaseDTO convertToExtensionDetalleDTO(Actividad actividad) {
        ExtensionDetalle detalle = extensionDetalleRepository
                .findByActividadOidActividad(actividad.getOidActividad())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró un detalle de extensión para la actividad con ID: "
                                + actividad.getOidActividad()));
        return buildActividadBaseDTO(actividad, detalle);
    }

    /**
     * Convierte una entidad Actividad con TrabajoInvestigacionDetalle a su DTO.
     */
    public ActividadBaseDTO convertToTrabajoInvestigacionDetalleDTO(Actividad actividad) {
        TrabajoInvestigacionDetalle detalle = trabajoInvestigacionDetalleRepository
                .findByActividadOidActividad(actividad.getOidActividad())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró un detalle de trabajo de investigación para la actividad con ID: "
                                + actividad.getOidActividad()));
        return buildActividadBaseDTO(actividad, detalle);
    }

    /**
     * Convierte una entidad Actividad y un detalle específico a su DTO
     * correspondiente.
     *
     * @param actividad La actividad base.
     * @param detalle   El detalle asociado a la actividad.
     * @return Un DTO de tipo ActividadBaseDTO.
     */
    private ActividadBaseDTO buildActividadBaseDTO(Actividad actividad, Object detalle) {
        UsuarioDTO evaluadorDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluador());

        return new ActividadBaseDTO(
                actividad.getOidActividad(),
                actividad.getTipoActividad(),
                actividad.getProceso().getOidProceso(),
                actividad.getEstadoActividad().getOidEstadoActividad(),
                actividad.getNombreActividad(),
                actividad.getHoras(),
                actividad.getSemanas(),
                actividad.getInformeEjecutivo(),
                actividad.getFechaCreacion(),
                actividad.getFechaActualizacion(),
                fuenteDTOService.convertToFuenteDTOList(actividad.getFuentes()),
                detalle,
                evaluadorDTO,
                actividad.getProceso().getEvaluado().getOidUsuario(),
                actividad.getProceso().getEvaluador().getOidUsuario());
    }

    /**
     * Convierte una entidad Actividad en su DTO correspondiente basado en su tipo
     * de actividad.
     */
    public ActividadBaseDTO convertActividadToDTO(Actividad actividad) {
        String tipoActividad = actividad.getTipoActividad().getNombre().toUpperCase();
        switch (tipoActividad) {
            case "DOCENCIA":
                return convertToDocenciaDetalleDTO(actividad);
            case "TRABAJO DE DOCENCIA":
                return convertToTrabajoDocenciaDetalleDTO(actividad);
            case "PROYECTO DE INVESTIGACIÓN":
                return convertToProyectoInvestigacionDetalleDTO(actividad);
            case "CAPACITACIÓN":
                return convertToCapacitacionDetalleDTO(actividad);
            case "ADMINISTRACIÓN":
                return convertToAdministracionDetalleDTO(actividad);
            case "OTRO SERVICIO":
                return convertToOtroServicioDetalleDTO(actividad);
            case "EXTENSIÓN":
                return convertToExtensionDetalleDTO(actividad);
            case "TRABAJO DE INVESTIGACIÓN":
                return convertToTrabajoInvestigacionDetalleDTO(actividad);
            default:
                throw new IllegalArgumentException("Tipo de actividad no reconocido: " + tipoActividad);
        }
    }

    private ActividadDTOEvaluador buildActividadDTOEvaluador(Actividad actividad, List<FuenteDTO> fuenteDTOs, UsuarioDTO evaluadoDTO) {
        return new ActividadDTOEvaluador(
                actividad.getOidActividad(),
                actividad.getNombreActividad(),
                actividad.getHoras(),
                actividad.getSemanas(),
                actividad.getEstadoActividad(),
                actividad.getInformeEjecutivo(),
                actividad.getFechaCreacion(),
                actividad.getFechaActualizacion(),
                actividad.getTipoActividad(),
                fuenteDTOs,
                evaluadoDTO
        );
    }

    public ActividadDTOEvaluador convertToDTOWithEvaluado(Actividad actividad) {
        UsuarioDTO evaluadoDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluado());
        List<FuenteDTO> fuenteDTOs = actividad.getFuentes().stream()
                .map(this::convertFuenteToDTO)
                .collect(Collectors.toList());

        return buildActividadDTOEvaluador(actividad, fuenteDTOs, evaluadoDTO);
    }

    public ActividadDTOEvaluador convertToDTOWithEvaluado(Actividad actividad, String tipoFuente, String estadoFuente) {
        UsuarioDTO evaluadoDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluado());
        List<FuenteDTO> fuenteDTOs = actividad.getFuentes().stream()
                .filter(fuente -> {
                    boolean tipoMatch = (tipoFuente == null || fuente.getTipoFuente().equalsIgnoreCase(tipoFuente));
                    boolean estadoMatch = (estadoFuente == null || fuente.getEstadoFuente().getNombreEstado().equalsIgnoreCase(estadoFuente));
                    return tipoMatch && estadoMatch;
                })
                .map(this::convertFuenteToDTO)
                .collect(Collectors.toList());

        return buildActividadDTOEvaluador(actividad, fuenteDTOs, evaluadoDTO);
    }

    /**
     * Convierte una entidad Usuario en un UsuarioDTO.
     * Usa valores predeterminados para nombres y apellidos si son nulos.
     *
     * @param evaluador La entidad Usuario a convertir.
     * @return El objeto UsuarioDTO convertido.
     */
    public UsuarioDTO convertToUsuarioDTO(Usuario evaluador) {
        List<RolDTO> rolDTOList = evaluador.getRoles().stream()
                .map(rol -> new RolDTO(rol.getNombre()))
                .collect(Collectors.toList());

        String nombres = evaluador.getNombres() != null ? evaluador.getNombres() : DEFAULT_NAME;
        String apellidos = evaluador.getApellidos() != null ? evaluador.getApellidos() : DEFAULT_NAME;

        return new UsuarioDTO(
            evaluador.getOidUsuario(),
            evaluador.getIdentificacion(),
            nombres,
            apellidos,
            rolDTOList
        );
    }

    /**
     * Convierte una entidad Fuente en un FuenteDTO.
     *
     * @param fuente La entidad Fuente a convertir.
     * @return El objeto FuenteDTO convertido.
     */
    public FuenteDTO convertFuenteToDTO(Fuente fuente) {
        return new FuenteDTO(
            fuente.getOidFuente(),
            fuente.getTipoFuente(),
            fuente.getCalificacion(),
            fuente.getNombreDocumentoFuente(),
            fuente.getNombreDocumentoInforme(),
            fuente.getObservacion(),
            fuente.getFechaCreacion(),
            fuente.getFechaActualizacion(),
            fuente.getEstadoFuente().getNombreEstado()
        );
    }
}
