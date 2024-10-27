package co.edu.unicauca.sed.api.service;

import org.springframework.stereotype.Service;

import co.edu.unicauca.sed.api.dto.ActividadDTO;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.RolDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
import co.edu.unicauca.sed.api.specification.ActividadSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    /**
     * Retrieves all activities along with their associated sources.
     * 
     * @return List of activities as DTOs
     */
    public List<ActividadDTO> findAll(boolean ascendingOrder) {
        // Convert Iterable to List
        List<Actividad> actividades = new ArrayList<>();
        actividadRepository.findAll().forEach(actividades::add);

        // Convert the list of activities to DTO
        List<ActividadDTO> actividadDTOs = actividades.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

        return sortActivities(actividadDTOs, ascendingOrder);
    }

    /**
     * Retrieves all activities that are part of active academic periods (state =
     * 1).
     * Converts the activities to DTO format.
     * 
     * @return List of activities as DTOs
     */
    public List<ActividadDTO> findAllInActivePeriods(boolean ascendingOrder) {
        // Fetch all activities where the academic period is active
        List<Actividad> actividades = actividadRepository.findByProceso_OidPeriodoAcademico_Estado(1);

        // Convert the list of activities to DTO
        List<ActividadDTO> actividadDTOs = actividades.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

        return sortActivities(actividadDTOs, ascendingOrder);
    }

    /**
     * Retrieves activities for an evaluator in active academic periods.
     */
    public List<ActividadDTO> findActivitiesByEvaluadoInActivePeriod(Integer oidUsuario, boolean ascendingOrder) {
        // Fetch activities for the evaluator where the academic period is active
        List<Actividad> actividades = actividadRepository.findByProceso_Evaluado_OidUsuarioAndProceso_OidPeriodoAcademico_Estado(oidUsuario, 1);

        // Convert the list of activities to DTO
        List<ActividadDTO> actividadDTOs = actividades.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

        return sortActivities(actividadDTOs, ascendingOrder);
    }

    /**
     * Retrieves activities for an evaluator.
     */
    public List<ActividadDTO> findActivitiesByEvaluado(Integer oidUsuario, boolean ascendingOrder) {
        // Fetch activities for the evaluator
        List<Actividad> actividades = actividadRepository.findByProceso_Evaluado_OidUsuario(oidUsuario);

        // Convert the list of activities to DTO
        List<ActividadDTO> actividadDTOs = actividades.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

        return sortActivities(actividadDTOs, ascendingOrder);
    }

    public List<ActividadDTO> findActivitiesWithFilters(String tipoActividad, String nombreEvaluador, List<String> roles, String tipoFuente, String estadoFuente, boolean ascendingOrder) {
        Specification<Actividad> spec = Specification.where(ActividadSpecification.hasTipoActividad(tipoActividad))
                .and(ActividadSpecification.hasNombreEvaluador(nombreEvaluador))
                .and(ActividadSpecification.hasRoles(roles))
                .and(ActividadSpecification.hasTipoFuente(tipoFuente))
                .and(ActividadSpecification.hasEstadoFuente(estadoFuente));
    
        List<Actividad> actividades = actividadRepository.findAll(spec);

        List<ActividadDTO> actividadDTOs = actividades.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return sortActivities(actividadDTOs, ascendingOrder);
    }

    /**
     * Finds an activity by its ID.
     */
    public Actividad findByOid(Integer oid) {
        return actividadRepository.findById(oid).orElse(null);
    }

    /**
     * Saves a new activity to the database.
     */
    public Actividad save(Actividad actividad) {
        return actividadRepository.save(actividad);
    }

    /**
     * Deletes an activity from the database by its ID.
     */
    public void delete(Integer oid) {
        actividadRepository.deleteById(oid);
    }

    private List<ActividadDTO> sortActivities(List<ActividadDTO> actividades, boolean ascendingOrder) {
        return actividades.stream()
                .sorted(ascendingOrder
                        ? Comparator.comparing(actividadDTO -> actividadDTO.getTipoActividad().getNombre())
                        : Comparator
                                .comparing((ActividadDTO actividadDTO) -> actividadDTO.getTipoActividad().getNombre())
                                .reversed())
                .collect(Collectors.toList());
    }

    /**
     * Converts an Actividad entity to ActividadDTO.
     */
    public ActividadDTO convertToDTO(Actividad actividad) {
        UsuarioDTO evaluadorDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluador());

        return new ActividadDTO(
                actividad.getCodigoActividad(),
                actividad.getNombre(),
                actividad.getHoras(),
                actividad.getFechaCreacion(),
                actividad.getFechaActualizacion(),
                actividad.getTipoActividad(),
                actividad.getFuentes().stream().map(this::convertFuenteToDTO).collect(Collectors.toList()),
                evaluadorDTO);
    }

    /**
     * Converts a Usuario entity to UsuarioDTO.
     */
    private UsuarioDTO convertToUsuarioDTO(Usuario evaluador) {
        List<RolDTO> rolDTOList = evaluador.getRoles().stream()
                .map(rol -> new RolDTO(rol.getNombre(), rol.getEstado()))
                .collect(Collectors.toList());

        String nombres = evaluador.getNombres() != null ? evaluador.getNombres() : "N/A";
        String apellidos = evaluador.getApellidos() != null ? evaluador.getApellidos() : "N/A";

        return new UsuarioDTO(
                evaluador.getOidUsuario(),
                evaluador.getUsuarioDetalle().getIdentificacion(),
                nombres,
                apellidos,
                rolDTOList);
    }

    /**
     * Converts a Fuente entity to FuenteDTO.
     */
    public FuenteDTO convertFuenteToDTO(Fuente fuente) {
        return new FuenteDTO(
                fuente.getOidFuente(),
                fuente.getTipoFuente(),
                fuente.getCalificacion(),
                fuente.getNombreDocumento(),
                fuente.getObservacion(),
                fuente.getFechaCreacion(),
                fuente.getFechaActualizacion(),
                fuente.getOidestadofuente().getNombreEstado());
    }
}
