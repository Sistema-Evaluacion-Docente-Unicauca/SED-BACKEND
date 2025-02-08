package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.dto.actividad.*;
import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.repository.ActividadRepository;
import co.edu.unicauca.sed.api.service.PeriodoAcademicoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
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

        Specification<Actividad> spec = filtrarActividades(evaluatorUserId, evaluatedUserId, activityCode, activityType,
                evaluatorName, roles, sourceType, sourceStatus, ascendingOrder, idPeriodoAcademico);

        Page<Actividad> activitiesPage = actividadRepository.findAll(spec, pageable);

        List<ActividadBaseDTO> activityDTOs = activitiesPage.getContent().stream().map(actividadDTOService::convertActividadToDTO).collect(Collectors.toList());

        return new PageImpl<>(activityDTOs, pageable, activitiesPage.getTotalElements());
    }

    public Page<ActividadDTOEvaluador> findActivitiesByEvaluador(Integer evaluatorUserId, Integer evaluatedUserId,
            String activityCode, String activityType, String evaluatorName, List<String> roles, String sourceType,
            String sourceStatus, Boolean ascendingOrder, Integer idPeriodoAcademico, Pageable pageable) {

        Specification<Actividad> spec = filtrarActividades(evaluatorUserId, evaluatedUserId, activityCode, activityType,
                evaluatorName, roles, sourceType, sourceStatus, ascendingOrder, idPeriodoAcademico);

        Page<Actividad> activitiesPage = actividadRepository.findAll(spec, pageable);

        List<ActividadDTOEvaluador> activityDTOs = activitiesPage.getContent().stream()
                .map(activity -> (sourceType != null || sourceStatus != null)
                        ? actividadDTOService.convertToDTOWithEvaluado(activity, sourceType, sourceStatus)
                        : actividadDTOService.convertToDTOWithEvaluado(activity))
                .collect(Collectors.toList());

        return new PageImpl<>(activityDTOs, pageable, activitiesPage.getTotalElements());
    }

    public Specification<Actividad> filtrarActividades(
            Integer userEvaluatorId, Integer userEvaluatedId, String activityCode, String activityType, String evaluatorName,
            List<String> roles, String sourceType, String sourceStatus, Boolean ascendingOrder, Integer idPeriodoAcademico) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Integer finalIdPeriodoAcademico = (idPeriodoAcademico != null) ? idPeriodoAcademico : periodoAcademicoService.obtenerPeriodoAcademicoActivo();

            predicates.add(cb.equal(root.get("proceso").get("oidPeriodoAcademico").get("oidPeriodoAcademico"), finalIdPeriodoAcademico));

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
                predicates.add(cb.equal(root.join("tipoActividad").get("oidTipoActividad"), Integer.parseInt(activityType)));
            }

            if (evaluatorName != null && !evaluatorName.isEmpty()) {
                predicates.add(cb.like(cb.concat(
                        root.join("proceso").join("evaluador").get("nombres"),
                        root.join("proceso").join("evaluador").get("apellidos")), "%" + evaluatorName + "%"));
            }

            if (sourceType != null || sourceStatus != null) {
                Join<Object, Object> sourceJoin = root.join("fuentes", JoinType.INNER);
                if (sourceType != null) {
                    predicates.add(cb.equal(sourceJoin.get("tipoFuente"), sourceType));
                }
                if (sourceStatus != null) {
                    predicates.add(cb.equal(sourceJoin.get("estadoFuente").get("oidEstadoFuente"), sourceStatus));
                }
            }

            query.distinct(true);

            aplicarOrdenacion(query, cb, root, ascendingOrder);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void aplicarOrdenacion(CriteriaQuery<?> query, CriteriaBuilder cb, Root<Actividad> root, Boolean ascendingOrder) {
        boolean isAscending = (ascendingOrder != null) ? ascendingOrder : DEFAULT_ASCENDING_ORDER;
        if (isAscending) {
            query.orderBy(cb.asc(root.get("nombreActividad")));
        } else {
            query.orderBy(cb.desc(root.get("nombreActividad")));
        }
    }
}
