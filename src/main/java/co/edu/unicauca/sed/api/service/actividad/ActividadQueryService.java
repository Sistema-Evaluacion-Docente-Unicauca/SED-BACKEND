package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.controller.ActividadController;
import co.edu.unicauca.sed.api.dto.actividad.*;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Fuente;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
import co.edu.unicauca.sed.api.service.PeriodoAcademicoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Servicio para realizar consultas avanzadas sobre actividades utilizando
 * Criteria API.
 */
@Service
public class ActividadQueryService {

    private static final Logger logger = LoggerFactory.getLogger(ActividadController.class);

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ActividadDTOService actividadDTOService;
    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;
    @Autowired
    private ActividadRepository actividadRepository;

    // Orden predeterminada de clasificación
    private static final boolean DEFAULT_ASCENDING_ORDER = true;

    public Page<ActividadBaseDTO> findActivitiesByEvaluado(
            Integer evaluatorUserId, Integer evaluatedUserId,
            String activityCode, String activityType, String evaluatorName, List<String> roles,
            String sourceType, String sourceStatus, Boolean ascendingOrder, Integer idPeriodoAcademico,
            Pageable pageable) {

        logger.info(
                "🔵 [FIND_BY_EVALUADO] Buscando actividades para evaluado con parámetros: evaluatorUserId={}, evaluatedUserId={}, activityCode={}",
                evaluatorUserId, evaluatedUserId, activityCode);

        try {
            Specification<Actividad> spec = filtrarActividades(evaluatorUserId, evaluatedUserId, activityCode,
                    activityType, evaluatorName, roles, sourceType, sourceStatus, ascendingOrder, idPeriodoAcademico);
            Page<Actividad> activitiesPage = actividadRepository.findAll(spec, pageable);
            logger.info("✅ [FIND_BY_EVALUADO] Se encontraron {} actividades para los parámetros dados.",
                    activitiesPage.getTotalElements());

            List<ActividadBaseDTO> activityDTOs = activitiesPage.getContent().stream()
                    .map(actividadDTOService::buildActividadBaseDTO)
                    .collect(Collectors.toList());
            return new PageImpl<>(activityDTOs, pageable, activitiesPage.getTotalElements());
        } catch (IllegalStateException e) {
            logger.warn("⚠️ [WARN] No se encontró un período académico activo.");
            throw e;
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error en findActivitiesByEvaluado: {}", e.getMessage(), e);
            throw new RuntimeException("Error inesperado al obtener actividades para evaluado.", e);
        }
    }

    public Page<ActividadDTOEvaluador> findActivitiesByEvaluador(
            Integer evaluatorUserId, Integer evaluatedUserId,
            String activityCode, String activityType, String evaluatorName, List<String> roles, String sourceType,
            String sourceStatus, Boolean ascendingOrder, Integer idPeriodoAcademico, Pageable pageable) {

        logger.info(
                "🔵 [FIND_BY_EVALUADOR] Buscando actividades para evaluador con parámetros: evaluatorUserId={}, evaluatedUserId={}, activityCode={}",
                evaluatorUserId, evaluatedUserId, activityCode);

        Specification<Actividad> spec = filtrarActividades(evaluatorUserId, evaluatedUserId, activityCode, activityType,
                evaluatorName, roles, sourceType, sourceStatus, ascendingOrder, idPeriodoAcademico);

        Page<Actividad> activitiesPage = actividadRepository.findAll(spec, pageable);

        if (activitiesPage == null) {
            logger.warn("⚠️ [FIND_BY_EVALUADO] La consulta devolvió NULL, revisa los filtros y el repositorio.");
            throw new RuntimeException("La consulta a la base de datos devolvió NULL, verifica la configuración.");
        }

        List<ActividadDTOEvaluador> activityDTOs = activitiesPage.stream()
                .map(activity -> (sourceType != null || sourceStatus != null)
                        ? actividadDTOService.convertToDTOWithEvaluado(activity, sourceType, sourceStatus)
                        : actividadDTOService.convertToDTOWithEvaluado(activity))
                .collect(Collectors.toList());

        return new PageImpl<>(activityDTOs, pageable, activitiesPage.getTotalElements());
    }

    public Specification<Actividad> filtrarActividades(
            Integer userEvaluatorId, Integer userEvaluatedId, String activityCode, String activityType,
            String evaluatorName, List<String> roles, String sourceType, String sourceStatus, Boolean ascendingOrder,
            Integer idPeriodoAcademico) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Integer finalIdPeriodoAcademico;
            try {
                finalIdPeriodoAcademico = (idPeriodoAcademico != null) ? idPeriodoAcademico
                        : periodoAcademicoService.obtenerIdPeriodoAcademicoActivo();
            } catch (IllegalStateException e) {
                logger.warn("⚠️ [PERIODO] No se encontró un período académico activo antes de ejecutar la consulta.");
                throw new EntityNotFoundException("No se encontró un período académico activo.");
            }
            predicates.add(cb.equal(root.get("proceso").get("oidPeriodoAcademico").get("oidPeriodoAcademico"),
                    finalIdPeriodoAcademico));

            if (userEvaluatorId != null) {
                predicates.add(cb.equal(root.join("proceso").join("evaluador").get("oidUsuario"), userEvaluatorId));
            }

            if (userEvaluatedId != null) {
                predicates.add(cb.equal(root.join("proceso").join("evaluado").get("oidUsuario"), userEvaluatedId));
            }

            if (activityCode != null && !activityCode.isEmpty()) {
                predicates.add(cb.like(root.get("nombreActividad"), "%" + activityCode + "%"));
            }

            if (activityType != null && !activityType.isEmpty()) {
                predicates.add(
                        cb.equal(root.join("tipoActividad").get("oidTipoActividad"), Integer.parseInt(activityType)));
            }

            if (evaluatorName != null && !evaluatorName.isEmpty()) {
                predicates.add(cb.like(cb.concat(
                        root.join("proceso").join("evaluador").get("nombres"),
                        root.join("proceso").join("evaluador").get("apellidos")), "%" + evaluatorName + "%"));
            }

            query.distinct(true);

            aplicarOrdenacion(query, cb, root, ascendingOrder, sourceType, sourceStatus);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void aplicarOrdenacion(CriteriaQuery<?> query, CriteriaBuilder cb, Root<Actividad> root,
            Boolean ascendingOrder, String sourceType, String sourceStatus) {
        boolean isAscending = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;
        List<Order> orderList = new ArrayList<>();

        boolean ordenarPorFuente = (sourceType != null && !sourceType.isEmpty())
                || (sourceStatus != null && !sourceStatus.isEmpty());

        if (ordenarPorFuente) {
            Join<Actividad, Fuente> fuenteJoin = root.join("fuentes", JoinType.LEFT);

            if (sourceType != null && !sourceType.isEmpty()) {
                orderList.add(
                        isAscending ? cb.asc(fuenteJoin.get("tipoFuente")) : cb.desc(fuenteJoin.get("tipoFuente")));
            }

            if (sourceStatus != null && !sourceStatus.isEmpty()) {
                orderList.add(isAscending ? cb.asc(fuenteJoin.get("estadoFuente").get("oidEstadoFuente"))
                        : cb.desc(fuenteJoin.get("estadoFuente").get("oidEstadoFuente")));
            }
        } else {
            orderList.add(isAscending ? cb.asc(root.get("nombreActividad")) : cb.desc(root.get("nombreActividad")));
        }

        if (!orderList.isEmpty()) {
            query.orderBy(orderList);
        }
    }
}
