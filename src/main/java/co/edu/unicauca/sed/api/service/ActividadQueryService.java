package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ActividadDTO;
import co.edu.unicauca.sed.api.dto.ActividadDTOEvaluador;
import co.edu.unicauca.sed.api.dto.FuenteDTO;
import co.edu.unicauca.sed.api.model.Actividad;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Servicio para realizar consultas avanzadas sobre actividades utilizando Criteria API.
 */
@Service
public class ActividadQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ActividadDTOService actividadDTOService;

    // Orden predeterminada de clasificación
    private static final boolean DEFAULT_ASCENDING_ORDER = true;

    /**
     * Recupera las actividades relacionadas con un evaluado en períodos académicos activos.
     *
     * @param evaluatorUserId ID del usuario evaluador.
     * @param evaluatedUserId ID del usuario evaluado.
     * @param activityCode    Código de la actividad.
     * @param activityType    Tipo de actividad.
     * @param evaluatorName   Nombre del evaluador.
     * @param roles           Lista de roles.
     * @param sourceType      Tipo de fuente.
     * @param sourceStatus    Estado de la fuente.
     * @param order           Orden de los resultados (ascendente/descendente).
     * @param isActivePeriod  Indica si los períodos deben ser activos.
     * @return Lista de actividades como DTO.
     */
    public List<ActividadDTO> findActivitiesByEvaluado(Integer evaluatorUserId, Integer evaluatedUserId,
                                                       String activityCode, String activityType, String evaluatorName,
                                                       List<String> roles, String sourceType, String sourceStatus,
                                                       Boolean order, Boolean isActivePeriod) {

        // Consultar actividades aplicando los filtros
        List<Actividad> activities = findActivitiesWithFilters(evaluatorUserId, evaluatedUserId, activityCode,
                activityType, evaluatorName, roles, sourceType, sourceStatus, order, isActivePeriod);

        // Convertir las actividades en DTOs
        return activities.stream().map(activity -> {
            ActividadDTO dto = (sourceType != null || sourceStatus != null)
                    ? actividadDTOService.convertToDTO(activity, sourceType, sourceStatus)
                    : actividadDTOService.convertToDTO(activity);

            // Ordenar las fuentes por tipoFuente
            dto.getFuentes().sort(Comparator.comparing(FuenteDTO::getTipoFuente));
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Recupera las actividades relacionadas con un evaluador.
     *
     * @param evaluatorUserId ID del usuario evaluador.
     * @param evaluatedUserId ID del usuario evaluado.
     * @param activityCode    Código de la actividad.
     * @param activityType    Tipo de actividad.
     * @param evaluatorName   Nombre del evaluador.
     * @param roles           Lista de roles.
     * @param sourceType      Tipo de fuente.
     * @param sourceStatus    Estado de la fuente.
     * @param ascendingOrder  Orden de los resultados (ascendente/descendente).
     * @param activePeriod    Indica si los períodos deben ser activos.
     * @return Lista de actividades como DTO con información del evaluador.
     */
    public List<ActividadDTOEvaluador> findActivitiesByEvaluador(Integer evaluatorUserId, Integer evaluatedUserId,
                                                                 String activityCode, String activityType,
                                                                 String evaluatorName, List<String> roles,
                                                                 String sourceType, String sourceStatus,
                                                                 Boolean ascendingOrder, Boolean activePeriod) {

        boolean isActivePeriod = (activePeriod != null) ? activePeriod : DEFAULT_ASCENDING_ORDER;

        // Consultar actividades aplicando los filtros
        List<Actividad> activities = findActivitiesWithFilters(evaluatorUserId, evaluatedUserId, activityCode,
                activityType, evaluatorName, roles, sourceType, sourceStatus, ascendingOrder, isActivePeriod);

        // Convertir las actividades en DTOs con información del evaluador
        return activities.stream().map(activity -> {
            ActividadDTOEvaluador dto = (sourceType != null || sourceStatus != null)
                    ? actividadDTOService.convertToDTOWithEvaluado(activity, sourceType, sourceStatus)
                    : actividadDTOService.convertToDTOWithEvaluado(activity);

            // Ordenar las fuentes por tipoFuente
            dto.getFuentes().sort(Comparator.comparing(FuenteDTO::getTipoFuente));
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Aplica filtros dinámicos sobre las actividades utilizando Criteria API.
     *
     * @param userEvaluatorId ID del evaluador.
     * @param userEvaluatedId ID del evaluado.
     * @param activityCode    Código de la actividad.
     * @param activityType    Tipo de actividad.
     * @param evaluatorName   Nombre del evaluador.
     * @param roles           Lista de roles del evaluador.
     * @param sourceType      Tipo de fuente.
     * @param sourceStatus    Estado de la fuente.
     * @param ascendingOrder  Orden de los resultados (ascendente/descendente).
     * @param isActivePeriod  Indica si los períodos deben ser activos.
     * @return Lista de actividades filtradas.
     */
    public List<Actividad> findActivitiesWithFilters(Integer userEvaluatorId, Integer userEvaluatedId,
                                                     String activityCode, String activityType, String evaluatorName,
                                                     List<String> roles, String sourceType, String sourceStatus,
                                                     Boolean ascendingOrder, Boolean isActivePeriod) {

        final String ATTRIBUTE_PROCESS = "proceso";
        final String ATTRIBUTE_EVALUATOR = "evaluador";
        final String ATTRIBUTE_EVALUATED = "evaluado";
        final String ATTRIBUTE_ROLES = "roles";
        final String ATTRIBUTE_USER_ID = "oidUsuario";
        final String ATTRIBUTE_NAME = "nombre";
        final String ATTRIBUTE_SOURCES = "fuentes";
        final String ATTRIBUTE_SOURCE_TYPE = "tipoFuente";
        final String ATTRIBUTE_SOURCE_STATUS = "estadoFuente";
        final String ATTRIBUTE_PERIOD_STATUS = "estado";
        final Boolean DEFAULT_ACTIVE_PERIOD = true;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Actividad> query = cb.createQuery(Actividad.class);
        Root<Actividad> root = query.from(Actividad.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filtros por evaluador, evaluado, código, tipo y otros parámetros
        if (userEvaluatorId != null) {
            predicates.add(cb.equal(root.join(ATTRIBUTE_PROCESS).join(ATTRIBUTE_EVALUATOR).get(ATTRIBUTE_USER_ID),
                    userEvaluatorId));
        }

        if (userEvaluatedId != null) {
            predicates.add(cb.equal(root.join(ATTRIBUTE_PROCESS).join(ATTRIBUTE_EVALUATED).get(ATTRIBUTE_USER_ID),
                    userEvaluatedId));
        }

        if (activityCode != null && !activityCode.isEmpty()) {
            predicates.add(cb.like(root.get("nombre"), "%" + activityCode + "%"));
        }

        if (activityType != null && !activityType.isEmpty()) {
            predicates.add(cb.like(root.join("tipoActividad").get(ATTRIBUTE_NAME), "%" + activityType + "%"));
        }

        if (evaluatorName != null && !evaluatorName.isEmpty()) {
            predicates.add(cb.like(cb.concat(
                    root.join(ATTRIBUTE_PROCESS).join(ATTRIBUTE_EVALUATOR).get("nombres"),
                    root.join(ATTRIBUTE_PROCESS).join(ATTRIBUTE_EVALUATOR).get("apellidos")),
                    "%" + evaluatorName + "%"));
        }

        // Filtro combinado por fuente y estado de fuente
        if (sourceType != null || sourceStatus != null) {
            Join<Object, Object> sourceJoin = root.join(ATTRIBUTE_SOURCES, JoinType.INNER);
            if (sourceType != null) {
                predicates.add(cb.equal(sourceJoin.get(ATTRIBUTE_SOURCE_TYPE), sourceType));
            }
            if (sourceStatus != null) {
                predicates.add(cb.equal(sourceJoin.get(ATTRIBUTE_SOURCE_STATUS).get("nombreEstado"), sourceStatus));
            }
        }

        // Filtro por período académico activo
        boolean periodStatus = (isActivePeriod != null) ? isActivePeriod : DEFAULT_ACTIVE_PERIOD;
        predicates.add(cb.equal(root.join(ATTRIBUTE_PROCESS).join("oidPeriodoAcademico").get(ATTRIBUTE_PERIOD_STATUS),
                periodStatus ? 1 : 2));

        // Aplicar filtros y ordenar resultados
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(ascendingOrder != null && ascendingOrder
                ? cb.asc(root.get(ATTRIBUTE_NAME))
                : cb.desc(root.get(ATTRIBUTE_NAME)));

        return entityManager.createQuery(query).getResultList();
    }
}
