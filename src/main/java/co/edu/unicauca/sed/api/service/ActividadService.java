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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;

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
    public List<ActividadDTO> findActivitiesByEvaluado(Integer userEvaluatorId, Integer userEvaluatedId, String tipoActividad, String nombreEvaluador,
            List<String> roles, String tipoFuente, String estadoFuente, Boolean order,
            Boolean isActivePeriod) {

        List<Actividad> actividades = findActivitiesWithFilters(userEvaluatorId, userEvaluatedId, tipoActividad, nombreEvaluador, roles,
                tipoFuente, estadoFuente, order, isActivePeriod);

        // Convert activities to DTOs
        List<ActividadDTO> actividadDTOs = actividades.stream().map(actividad -> {
            if (tipoFuente != null || estadoFuente != null) {
                return convertToDTO(actividad, tipoFuente, estadoFuente);
            } else {
                return convertToDTO(actividad);
            }
        }).collect(Collectors.toList());

        return actividadDTOs;
    }

    /**
     * Retrieves activities for an evaluator.
     */
    public List<ActividadDTOEvaluador> findActivitiesByEvaluador(Integer userEvaluatorId, Integer userEvaluatedId, String tipoActividad,
            String nombreEvaluador, List<String> roles, String tipoFuente, String estadoFuente, Boolean ascendingOrder,
            Boolean activePeriod) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;
        boolean isActivePeriod = (activePeriod != null) ? activePeriod : DEFAULT_ASCENDING_ORDER;

        List<Actividad> actividades = findActivitiesWithFilters(userEvaluatorId, userEvaluatedId, tipoActividad, nombreEvaluador, roles,
                tipoFuente, estadoFuente, ascendingOrder, isActivePeriod);

        // Convert activities to DTOs
        List<ActividadDTOEvaluador> actividadDTOs = actividades.stream().map(actividad -> {
            if (tipoFuente != null || estadoFuente != null) {
                return convertToDTOWithEvaluado(actividad, tipoFuente, estadoFuente);
            } else {
                return convertToDTOWithEvaluado(actividad);
            }
        }).collect(Collectors.toList());

        return actividadDTOs;
    }

    public List<Actividad> findActivitiesWithFilters(Integer userEvaluatorId, Integer userEvaluatedId, String activityType, String evaluatorName,
            List<String> roles, String sourceType, String sourceStatus, Boolean ascendingOrder,
            Boolean isActivePeriod) {

        final String ATTRIBUTE_PROCESS = "proceso";
        final String ATTRIBUTE_EVALUATOR = "evaluador";
        final String ATTRIBUTE_EVALUATED = "evaluado";
        final String ATTRIBUTE_ROLES = "roles";
        final String ATTRIBUTE_USER_ID = "oidUsuario";
        final String ATTRIBUTE_ACTIVITY_TYPE = "tipoActividad";
        final String ATTRIBUTE_NAME = "nombre";
        final String ATTRIBUTE_FIRST_NAME = "nombres";
        final String ATTRIBUTE_LAST_NAME = "apellidos";
        final String ATTRIBUTE_SOURCES = "fuentes";
        final String ATTRIBUTE_SOURCE_TYPE = "tipoFuente";
        final String ATTRIBUTE_SOURCE_STATUS = "estadoFuente";
        final String ATTRIBUTE_STATUS_NAME = "nombreEstado";
        final String ATTRIBUTE_PERIOD_STATUS = "estado";
        final Boolean DEFAULT_ACTIVE_PERIOD = true;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Actividad> query = cb.createQuery(Actividad.class);
        Root<Actividad> root = query.from(Actividad.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filter by userId in evaluated
        if (userEvaluatorId != null) {
            Join<Object, Object> evaluatorJoin = root.join(ATTRIBUTE_PROCESS).join(ATTRIBUTE_EVALUATED);
            Predicate evaluatorPredicate = cb.equal(evaluatorJoin.get(ATTRIBUTE_USER_ID), userEvaluatorId);
            predicates.add(cb.or(evaluatorPredicate));
        }

        // Filter by userId in evaluator
        if (userEvaluatedId != null) {
            Join<Object, Object> evaluatedJoin = root.join(ATTRIBUTE_PROCESS).join(ATTRIBUTE_EVALUATOR);
            Predicate evaluatedPredicate = cb.equal(evaluatedJoin.get(ATTRIBUTE_USER_ID), userEvaluatedId);
            predicates.add(cb.or(evaluatedPredicate));
        }

        // Filter by activityType
        if (activityType != null && !activityType.isEmpty()) {
            predicates.add(cb.equal(root.get(ATTRIBUTE_ACTIVITY_TYPE).get(ATTRIBUTE_NAME), activityType));
        }

        // Filter by evaluatorName
        if (evaluatorName != null && !evaluatorName.isEmpty()) {
            Join<?, ?> processJoin = root.join(ATTRIBUTE_PROCESS);
            predicates.add(
                cb.like(
                    cb.concat(
                        processJoin.get(ATTRIBUTE_EVALUATOR).get(ATTRIBUTE_FIRST_NAME),
                        processJoin.get(ATTRIBUTE_EVALUATOR).get(ATTRIBUTE_LAST_NAME)),
                    "%" + evaluatorName + "%"));
        }

        // Filter by roles in the evaluator within Process
        if (roles != null && !roles.isEmpty()) {
            Join<?, ?> evaluatorJoin = root.join(ATTRIBUTE_PROCESS).join(ATTRIBUTE_EVALUATOR);
            predicates.add(evaluatorJoin.join(ATTRIBUTE_ROLES).get(ATTRIBUTE_NAME).in(roles));
        }

        // Combined filter for sourceType and sourceStatus in sources
        if (sourceType != null || sourceStatus != null) {
            Join<?, ?> sourceJoin = root.join(ATTRIBUTE_SOURCES, JoinType.INNER);

            if (sourceType != null) {
                predicates.add(cb.equal(sourceJoin.get(ATTRIBUTE_SOURCE_TYPE), sourceType));
            }
            if (sourceStatus != null) {
                predicates.add(cb.equal(sourceJoin.get(ATTRIBUTE_SOURCE_STATUS).get(ATTRIBUTE_STATUS_NAME), sourceStatus));
            }
        }


        boolean periodStatus = (isActivePeriod != null) ? isActivePeriod : DEFAULT_ACTIVE_PERIOD;
        Join<Object, Object> periodJoin = root.join(ATTRIBUTE_PROCESS).join("oidPeriodoAcademico");
        int status = periodStatus ? 1 : 2; // Set status to 1 if isActivePeriod is true, otherwise 2
        predicates.add(cb.equal(periodJoin.get(ATTRIBUTE_PERIOD_STATUS), status));

        // Apply predicates to the query
        query.where(predicates.toArray(new Predicate[0]));

        // Sort based on ascendingOrder parameter
        boolean order = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;
        query.orderBy(order ? cb.asc(root.get(ATTRIBUTE_NAME)) : cb.desc(root.get(ATTRIBUTE_NAME)));

        // Execute query
        return entityManager.createQuery(query).getResultList();
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
        Comparator<ActividadDTO> comparator = Comparator
                .comparing(actividadDTO -> actividadDTO.getTipoActividad().getNombre());

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
    private List<ActividadDTOEvaluador> sortActivitiesEvaluador(List<ActividadDTOEvaluador> actividades,
            boolean ascendingOrder) {
        // Determine the comparator based on the ascendingOrder flag
        Comparator<ActividadDTOEvaluador> comparator = Comparator.comparing(
                actividadDTOEvaluador -> actividadDTOEvaluador.getTipoActividad().getNombre());

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
                fuente -> (tipoFuente == null || tipoFuente.equals(fuente.getTipoFuente()))
                        && (estadoFuente == null || estadoFuente.equals(fuente.getEstadoFuente().getNombreEstado())))
                .map(this::convertFuenteToDTO).collect(Collectors.toList());

        ActividadDTO actividadDTO = new ActividadDTO(
                actividad.getCodigoActividad(),
                actividad.getNombre(),
                actividad.getHoras(),
                actividad.getFechaCreacion(),
                actividad.getFechaActualizacion(),
                actividad.getTipoActividad(),
                filteredFuentes,
                evaluadorDTO);

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
                evaluadorDTO);
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
                evaluadoDTO);

        return actividadDTOEvaluador;
    }

    /**
     * Converts an Actividad entity to ActividadDTOEvaluador.
     */
    public ActividadDTOEvaluador convertToDTOWithEvaluado(Actividad actividad, String tipoFuente, String estadoFuente) {
        UsuarioDTO evaluadoDTO = convertToUsuarioDTO(actividad.getProceso().getEvaluado());

        List<FuenteDTO> fuenteDTOs = actividad.getFuentes().stream().map(this::convertFuenteToDTO)
                .collect(Collectors.toList());

        ActividadDTOEvaluador actividadDTOEvaluador = new ActividadDTOEvaluador(
                actividad.getCodigoActividad(),
                actividad.getNombre(),
                actividad.getHoras(),
                actividad.getFechaCreacion(),
                actividad.getFechaActualizacion(),
                actividad.getTipoActividad(),
                fuenteDTOs,
                evaluadoDTO);

        return actividadDTOEvaluador;
    }

    /**
     * Converts a Usuario entity to UsuarioDTO.
     */
    private UsuarioDTO convertToUsuarioDTO(Usuario evaluador) {

        List<RolDTO> rolDTOList = evaluador.getRoles().stream().map(rol -> new RolDTO(rol.getNombre(), rol.getEstado()))
                .collect(Collectors.toList());
        String nombres = evaluador.getNombres() != null ? evaluador.getNombres() : DEFAULT_NAME;
        String apellidos = evaluador.getApellidos() != null ? evaluador.getApellidos() : DEFAULT_NAME;

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                evaluador.getOidUsuario(),
                evaluador.getUsuarioDetalle().getIdentificacion(),
                nombres,
                apellidos,
                rolDTOList);

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
                fuente.getEstadoFuente().getNombreEstado());

        return fuenteDTO;
    }
}
