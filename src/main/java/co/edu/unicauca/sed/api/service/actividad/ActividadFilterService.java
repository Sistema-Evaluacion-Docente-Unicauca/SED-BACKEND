package co.edu.unicauca.sed.api.service.actividad;

import co.edu.unicauca.sed.api.model.Actividad;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para aplicar filtros dinámicos en la búsqueda de actividades utilizando Criteria API.
 */
@Service
public class ActividadFilterService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Busca actividades aplicando filtros dinámicos basados en los parámetros proporcionados.
     *
     * @param userEvaluatorId ID del usuario evaluador para filtrar actividades asignadas por él.
     * @param userEvaluatedId ID del usuario evaluado para filtrar actividades asignadas a él.
     * @param activityCode    Código de la actividad para búsqueda por coincidencia parcial.
     * @param activityType    Tipo de actividad para filtrar por tipo.
     * @param evaluatorName   Nombre del evaluador para búsqueda por coincidencia parcial.
     * @param roles           Lista de roles para filtrar actividades relacionadas con roles específicos.
     * @param sourceType      Tipo de fuente para filtrar actividades relacionadas con un tipo de fuente específico.
     * @param sourceStatus    Estado de la fuente para filtrar actividades relacionadas con un estado específico.
     * @param ascendingOrder  Indica si los resultados deben ordenarse en orden ascendente.
     * @param isActivePeriod  Indica si los resultados deben limitarse a períodos activos.
     * @return Lista de actividades que cumplen con los filtros proporcionados.
     */
    public List<Actividad> findActivitiesWithFilters(Integer userEvaluatorId, Integer userEvaluatedId, String activityCode,
                                                     String activityType, String evaluatorName, List<String> roles,
                                                     String sourceType, String sourceStatus, Boolean ascendingOrder,
                                                     Boolean isActivePeriod) {

        // Inicia el constructor de consultas
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Actividad> query = cb.createQuery(Actividad.class);
        Root<Actividad> root = query.from(Actividad.class);

        // Lista de predicados para almacenar los filtros dinámicos
        List<Predicate> predicates = new ArrayList<>();

        // Filtro por ID de evaluador
        if (userEvaluatorId != null) {
            Join<Object, Object> evaluatorJoin = root.join("proceso").join("evaluador");
            predicates.add(cb.equal(evaluatorJoin.get("oidUsuario"), userEvaluatorId));
        }

        // Filtro por ID de evaluado
        if (userEvaluatedId != null) {
            Join<Object, Object> evaluatedJoin = root.join("proceso").join("evaluado");
            predicates.add(cb.equal(evaluatedJoin.get("oidUsuario"), userEvaluatedId));
        }

        // Filtro por código de actividad
        if (activityCode != null && !activityCode.isEmpty()) {
            predicates.add(cb.like(root.get("codigoActividad"), "%" + activityCode + "%"));
        }

        // Filtro por tipo de actividad
        if (activityType != null && !activityType.isEmpty()) {
            predicates.add(cb.like(root.join("tipoActividad").get("nombre"), "%" + activityType + "%"));
        }

        // Filtro por nombre del evaluador
        if (evaluatorName != null && !evaluatorName.isEmpty()) {
            predicates.add(cb.like(root.join("proceso").join("evaluador").get("nombre"), "%" + evaluatorName + "%"));
        }

        // Aplicar filtros acumulados
        query.where(predicates.toArray(new Predicate[0]));

        // Ordenar resultados según el parámetro ascendingOrder
        if (ascendingOrder != null && ascendingOrder) {
            query.orderBy(cb.asc(root.get("nombre")));
        } else {
            query.orderBy(cb.desc(root.get("nombre")));
        }

        // Retorna la lista de actividades filtradas
        return entityManager.createQuery(query).getResultList();
    }
}
