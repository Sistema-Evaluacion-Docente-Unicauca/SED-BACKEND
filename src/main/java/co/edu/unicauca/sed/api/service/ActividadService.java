package co.edu.unicauca.sed.api.service;

import org.springframework.stereotype.Service;
import co.edu.unicauca.sed.api.dto.ActividadDTO;
import co.edu.unicauca.sed.api.dto.ActividadDTOEvaluador;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.dto.RolDTO;
import co.edu.unicauca.sed.api.dto.UsuarioDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.model.Usuario;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
import co.edu.unicauca.sed.api.specification.ActividadSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // Constants for status
    private static final int ACTIVE_PERIOD_STATUS = 1;
    private static final String DEFAULT_NAME = "N/A";
    // Default sort order
    private static final boolean DEFAULT_ASCENDING_ORDER = true;

    /**
     * Retrieves all activities along with their associated sources.
     */
    public List<ActividadDTO> findAll(Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;
        List<Actividad> actividades = new ArrayList<>();
        actividadRepository.findAll().forEach(actividades::add);
        List<ActividadDTO> actividadDTOs = actividades.stream().map(this::convertToDTO).collect(Collectors.toList());
        return sortActivities(actividadDTOs, order);
    }

    /**
     * Retrieves all activities that are part of active academic periods.
     */
    public List<ActividadDTO> findAllInActivePeriods(Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;
        List<Actividad> actividades = actividadRepository.findByProceso_OidPeriodoAcademico_Estado(ACTIVE_PERIOD_STATUS);
        List<ActividadDTO> actividadDTOs = actividades.stream().map(this::convertToDTO).collect(Collectors.toList());
        return sortActivities(actividadDTOs, order);
    }

    /**
     * Retrieves activities for an evaluator in active academic periods.
     */
    public List<ActividadDTO> findActivitiesByEvaluadoInActivePeriod(Integer oidUsuario, Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;
        List<Actividad> actividades = actividadRepository.findByProceso_Evaluado_OidUsuarioAndProceso_OidPeriodoAcademico_Estado(oidUsuario, ACTIVE_PERIOD_STATUS);
        List<ActividadDTO> actividadDTOs = actividades.stream().map(this::convertToDTO).collect(Collectors.toList());
        return sortActivities(actividadDTOs, order);
    }

    /**
     * Retrieves activities for an evaluator in active academic periods.
     */
    public List<ActividadDTOEvaluador> findActivitiesByEvaluadorInActivePeriod(Integer oidUsuario, Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;
        List<Actividad> actividades = actividadRepository.findByProceso_Evaluador_OidUsuarioAndProceso_OidPeriodoAcademico_Estado(oidUsuario, ACTIVE_PERIOD_STATUS);
        List<ActividadDTOEvaluador> actividadDTOs = actividades.stream().map(this::convertToDTOWithEvaluado).collect(Collectors.toList());
        return sortActivitiesEvaluador(actividadDTOs, order);
    }

    /**
     * Retrieves activities for an evaluator.
     */
    public List<ActividadDTO> findActivitiesByEvaluado(Integer oidUsuario, Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;
        List<Actividad> actividades = actividadRepository.findByProceso_Evaluado_OidUsuario(oidUsuario);
        List<ActividadDTO> actividadDTOs = actividades.stream().map(this::convertToDTO).collect(Collectors.toList());
        return sortActivities(actividadDTOs, order);
    }

    public List<ActividadDTO> findActivitiesWithFilters(String tipoActividad, String nombreEvaluador, List<String> roles, String tipoFuente, String estadoFuente, Boolean ascendingOrder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Actividad> query = cb.createQuery(Actividad.class);
        Root<Actividad> root = query.from(Actividad.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filtro por tipoActividad
        if (tipoActividad != null && !tipoActividad.isEmpty()) {
            predicates.add(cb.equal(root.get("tipoActividad").get("nombre"), tipoActividad));
        }

        // Filtro por nombreEvaluador
        if (nombreEvaluador != null && !nombreEvaluador.isEmpty()) {
            Join<?, ?> procesoJoin = root.join("proceso"); // Join con Proceso
            predicates.add(cb.like(
                cb.concat(procesoJoin.get("evaluador").get("nombres"), 
                          procesoJoin.get("evaluador").get("apellidos")),
                "%" + nombreEvaluador + "%"
            ));
        }

        // Filtro por roles en el evaluador dentro de Proceso
        if (roles != null && !roles.isEmpty()) {
            Join<?, ?> evaluadorJoin = root.join("proceso").join("evaluador");
            predicates.add(evaluadorJoin.join("roles").get("nombre").in(roles));
        }

        // Filtro combinado para tipoFuente y estadoFuente en las fuentes
        if (tipoFuente != null || estadoFuente != null) {
            Join<?, ?> fuenteJoin = root.join("fuentes", JoinType.INNER);

            if (tipoFuente != null) {
                predicates.add(cb.equal(fuenteJoin.get("tipoFuente"), tipoFuente));
            }
            if (estadoFuente != null) {
                predicates.add(cb.equal(fuenteJoin.get("estadoFuente").get("nombreEstado"), estadoFuente));
            }
        }

        // Aplica los predicados a la consulta
        query.where(predicates.toArray(new Predicate[0]));

        // Ordena de acuerdo al parámetro ascendingOrder
        boolean order = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;
        query.orderBy(order ? cb.asc(root.get("nombre")) : cb.desc(root.get("nombre")));

        // Ejecuta la consulta
        List<Actividad> actividades = entityManager.createQuery(query).getResultList();

        // Convierte las actividades a DTOs
        List<ActividadDTO> actividadDTOs = actividades.stream()
                .map(actividad -> {
                    if (tipoFuente != null || estadoFuente != null) {
                        return convertToDTO(actividad, tipoFuente, estadoFuente); // Filtra fuentes en el DTO
                    } else {
                        return convertToDTO(actividad); // Devuelve todas las fuentes en el DTO
                    }
                })
                .collect(Collectors.toList());

        return actividadDTOs;
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

    /**
     * Sorts a list of ActividadDTO by the name of their activity type.
     * If ascendingOrder is true, sorts in ascending order; otherwise, in descending
     * order.
     * 
     * @param actividades    List of ActividadDTO to be sorted
     * @param ascendingOrder Boolean indicating the sort order
     * @return Sorted list of ActividadDTO
     */
    private List<ActividadDTO> sortActivities(List<ActividadDTO> actividades, boolean ascendingOrder) {

        // Determine the comparator based on the ascendingOrder flag
        Comparator<ActividadDTO> comparator = Comparator.comparing(actividadDTO -> actividadDTO.getTipoActividad().getNombre());

        if (!ascendingOrder) {
            comparator = comparator.reversed();
        }

        // Sort and return the list
        return actividades.stream().sorted(comparator).collect(Collectors.toList());
    }

    /**
     * Sorts a list of ActividadDTOEvaluador by the name of their activity type.
     * If ascendingOrder is true, sorts in ascending order; otherwise, in descending
     * order.
     * 
     * @param actividades    List of ActividadDTOEvaluador to be sorted
     * @param ascendingOrder Boolean indicating the sort order
     * @return Sorted list of ActividadDTOEvaluador
     */
    private List<ActividadDTOEvaluador> sortActivitiesEvaluador(List<ActividadDTOEvaluador> actividades, boolean ascendingOrder) {
        // Determine the comparator based on the ascendingOrder flag
        Comparator<ActividadDTOEvaluador> comparator = Comparator.comparing(
            actividadDTOEvaluador -> actividadDTOEvaluador.getTipoActividad().getNombre()
        );

        if (!ascendingOrder) {
            comparator = comparator.reversed();
        }

        // Sort and return the list
        return actividades.stream().sorted(comparator).collect(Collectors.toList());
    }

    /**
     * Converts an Actividad entity to an ActividadDTO.
     * Includes mapping for evaluador and fuentes fields.
     *
     * @param actividad The Actividad entity to convert
     * @return The converted ActividadDTO
     */
    public ActividadDTO convertToDTO(Actividad actividad, String tipoFuente, String estadoFuente) {
        // Convert evaluador to DTO format
        UsuarioDTO evaluadorDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluador());

        // Create the ActividadDTO with mapped fields
        List<FuenteDTO> filteredFuentes = actividad.getFuentes().stream().filter(
                fuente -> (tipoFuente == null || tipoFuente.equals(fuente.getTipoFuente())) && (estadoFuente == null || estadoFuente.equals(fuente.getEstadoFuente().getNombreEstado()))
            ).map(this::convertFuenteToDTO).collect(Collectors.toList());

        ActividadDTO actividadDTO = new ActividadDTO(
            actividad.getCodigoActividad(),
            actividad.getNombre(),
            actividad.getHoras(),
            actividad.getFechaCreacion(),
            actividad.getFechaActualizacion(),
            actividad.getTipoActividad(),
            filteredFuentes,
            evaluadorDTO
        );

        return actividadDTO;
    }

    public ActividadDTO convertToDTO(Actividad actividad) {
        UsuarioDTO evaluadorDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluador());

        List<FuenteDTO> fuenteDTOs = actividad.getFuentes().stream().map(this::convertFuenteToDTO)
                .collect(Collectors.toList());

        ActividadDTO actividadDTO = new ActividadDTO(
            actividad.getCodigoActividad(),
            actividad.getNombre(),
            actividad.getHoras(),
            actividad.getFechaCreacion(),
            actividad.getFechaActualizacion(),
            actividad.getTipoActividad(),
            fuenteDTOs,
            evaluadorDTO
        );
        return actividadDTO;
    }

    /**
     * Converts an Actividad entity to ActividadDTOEvaluador.
     */
    public ActividadDTOEvaluador convertToDTOWithEvaluado(Actividad actividad) {
        UsuarioDTO evaluadoDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluado());

        ActividadDTOEvaluador actividadDTOEvaluador = new ActividadDTOEvaluador(
            actividad.getCodigoActividad(),
            actividad.getNombre(),
            actividad.getHoras(),
            actividad.getFechaCreacion(),
            actividad.getFechaActualizacion(),
            actividad.getTipoActividad(),
            actividad.getFuentes().stream().map(this::convertFuenteToDTO).collect(Collectors.toList()),
            evaluadoDTO
        );

        return actividadDTOEvaluador;
    }

    /**
     * Converts a Usuario entity to UsuarioDTO.
     */
    private UsuarioDTO convertToUsuarioDTO(Usuario evaluador) {

        List<RolDTO> rolDTOList = evaluador.getRoles().stream().map(rol -> new RolDTO(rol.getNombre(), rol.getEstado())).collect(Collectors.toList());
        String nombres = evaluador.getNombres() != null ? evaluador.getNombres() : DEFAULT_NAME;
        String apellidos = evaluador.getApellidos() != null ? evaluador.getApellidos() : DEFAULT_NAME;

        UsuarioDTO usuarioDTO = new UsuarioDTO(
            evaluador.getOidUsuario(),
            evaluador.getUsuarioDetalle().getIdentificacion(),
            nombres,
            apellidos,
            rolDTOList
        );

        return usuarioDTO;
    }

    /**
     * Converts a Fuente entity to FuenteDTO.
     */
    public FuenteDTO convertFuenteToDTO(Fuente fuente) {

        FuenteDTO fuenteDTO = new FuenteDTO(
            fuente.getOidFuente(),
            fuente.getTipoFuente(),
            fuente.getCalificacion(),
            fuente.getNombreDocumento(),
            fuente.getObservacion(),
            fuente.getFechaCreacion(),
            fuente.getFechaActualizacion(),
            fuente.getEstadoFuente().getNombreEstado()
        );

        return fuenteDTO;
    }
}
