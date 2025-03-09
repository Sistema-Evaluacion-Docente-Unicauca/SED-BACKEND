package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.domain.*;
import co.edu.unicauca.sed.api.dto.AtributoDTO;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.RolDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;
import co.edu.unicauca.sed.api.dto.actividad.*;
import co.edu.unicauca.sed.api.service.EavAtributoService;
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
    private EavAtributoService eavAtributoService;

    /**
     * Convierte una entidad Actividad y un detalle específico a su DTO
     * correspondiente.
     *
     * @param actividad La actividad base.
     * @param detalle   El detalle asociado a la actividad.
     * @return Un DTO de tipo ActividadBaseDTO.
     */
    public ActividadBaseDTO buildActividadBaseDTO(Actividad actividad) {
        UsuarioDTO evaluadorDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluador());

        // 🔹 Obtener los atributos dinámicos en formato AtributoDTO
        List<AtributoDTO> atributos = eavAtributoService.obtenerAtributosPorActividad(actividad);

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
                atributos,
                evaluadorDTO,
                actividad.getProceso().getEvaluado().getOidUsuario(),
                actividad.getProceso().getEvaluador().getOidUsuario());
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
