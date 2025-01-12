package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.dto.ActividadDTO;
import co.edu.unicauca.sed.api.dto.ActividadDTOEvaluador;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.RolDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de convertir entidades del modelo a DTOs relacionados con Actividades.
 */
@Service
public class ActividadDTOService {

    private static final String DEFAULT_NAME = "N/A";

    /**
     * Convierte una entidad Actividad en un ActividadDTO.
     * Incluye la conversión de los campos evaluador y fuentes.
     *
     * @param actividad    La entidad Actividad a convertir.
     * @param tipoFuente   Filtro para el tipo de fuente (opcional).
     * @param estadoFuente Filtro para el estado de la fuente (opcional).
     * @return El objeto ActividadDTO convertido.
     */
    public ActividadDTO convertToDTO(Actividad actividad, String tipoFuente, String estadoFuente) {
        // Convertir el evaluador a DTO
        UsuarioDTO evaluadorDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluador());

        // Filtrar y convertir las fuentes a DTO
        List<FuenteDTO> filteredFuentes = actividad.getFuentes().stream().filter(
                fuente -> (tipoFuente == null || tipoFuente.equals(fuente.getTipoFuente()))
                        && (estadoFuente == null || estadoFuente.equals(fuente.getEstadoFuente().getNombreEstado())))
                .map(this::convertFuenteToDTO).collect(Collectors.toList());

        return new ActividadDTO(
                actividad.getOidActividad(),
                actividad.getCodigoActividad(),
                actividad.getNombre(),
                actividad.getHorasTotales(),
                actividad.getCodVRI(),
                actividad.getEstadoActividad(),
                actividad.getInformeEjecutivo(),
                actividad.getFechaCreacion(),
                actividad.getFechaActualizacion(),
                actividad.getTipoActividad(),
                filteredFuentes,
                evaluadorDTO);
    }

    /**
     * Convierte una entidad Actividad en un ActividadDTO sin filtros.
     *
     * @param actividad La entidad Actividad a convertir.
     * @return El objeto ActividadDTO convertido.
     */
    public ActividadDTO convertToDTO(Actividad actividad) {
        UsuarioDTO evaluadorDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluador());

        List<FuenteDTO> fuenteDTOs = actividad.getFuentes().stream().map(this::convertFuenteToDTO)
                .collect(Collectors.toList());

        return new ActividadDTO(
                actividad.getOidActividad(),
                actividad.getCodigoActividad(),
                actividad.getNombre(),
                actividad.getHorasTotales(),
                actividad.getInformeEjecutivo(),
                actividad.getCodVRI(),
                actividad.getEstadoActividad(),
                actividad.getFechaCreacion(),
                actividad.getFechaActualizacion(),
                actividad.getTipoActividad(),
                fuenteDTOs,
                evaluadorDTO);
    }

    /**
     * Convierte una entidad Actividad en un ActividadDTOEvaluador.
     * Incluye los detalles del evaluado y las fuentes asociadas.
     *
     * @param actividad La entidad Actividad a convertir.
     * @return El objeto ActividadDTOEvaluador convertido.
     */
    public ActividadDTOEvaluador convertToDTOWithEvaluado(Actividad actividad) {
        UsuarioDTO evaluadoDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluado());
    
        return new ActividadDTOEvaluador(
                actividad.getOidActividad(),
                actividad.getCodigoActividad(),
                actividad.getNombre(),
                actividad.getHorasTotales(),
                actividad.getCodVRI(),
                actividad.getEstadoActividad(),
                actividad.getInformeEjecutivo(),
                actividad.getFechaCreacion(),
                actividad.getFechaActualizacion(),
                actividad.getTipoActividad(),
                actividad.getFuentes().stream().map(this::convertFuenteToDTO).collect(Collectors.toList()),
                evaluadoDTO
        );
    }    

    /**
     * Convierte una entidad Actividad en un ActividadDTOEvaluador.
     * Permite aplicar filtros por tipo y estado de fuente.
     *
     * @param actividad    La entidad Actividad a convertir.
     * @param tipoFuente   Filtro para el tipo de fuente (opcional).
     * @param estadoFuente Filtro para el estado de la fuente (opcional).
     * @return El objeto ActividadDTOEvaluador convertido.
     */
    public ActividadDTOEvaluador convertToDTOWithEvaluado(Actividad actividad, String tipoFuente, String estadoFuente) {
        UsuarioDTO evaluadoDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluado());

        List<FuenteDTO> fuenteDTOs = actividad.getFuentes().stream().map(this::convertFuenteToDTO)
                .collect(Collectors.toList());

        return new ActividadDTOEvaluador(
                actividad.getOidActividad(),
                actividad.getCodigoActividad(),
                actividad.getNombre(),
                actividad.getHorasTotales(),
                actividad.getCodVRI(),
                actividad.getEstadoActividad(),
                actividad.getInformeEjecutivo(),
                actividad.getFechaCreacion(),
                actividad.getFechaActualizacion(),
                actividad.getTipoActividad(),
                fuenteDTOs,
                evaluadoDTO);
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
                fuente.getEstadoFuente().getNombreEstado());
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
                .map(rol -> new RolDTO(rol.getNombre(), rol.getEstado()))
                .collect(Collectors.toList());

        String nombres = evaluador.getNombres() != null ? evaluador.getNombres() : DEFAULT_NAME;
        String apellidos = evaluador.getApellidos() != null ? evaluador.getApellidos() : DEFAULT_NAME;

        return new UsuarioDTO(
                evaluador.getOidUsuario(),
                evaluador.getUsuarioDetalle().getIdentificacion(),
                nombres,
                apellidos,
                rolDTOList);
    }
}
