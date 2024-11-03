package co.edu.unicauca.sed.api.specification;

import org.springframework.data.jpa.domain.Specification;

import co.edu.unicauca.sed.api.model.Actividad;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class ActividadSpecification {
    public static Specification<Actividad> hasFilters(String tipoActividad, String nombreEvaluador, List<String> roles, String tipoFuente, String estadoFuente) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por tipoActividad
            if (tipoActividad != null) {
                predicates.add(builder.equal(root.get("tipoActividad").get("nombre"), tipoActividad));
            }

            // Filtro por nombreEvaluador
            if (nombreEvaluador != null) {
                predicates.add(builder.like(
                        builder.concat(root.join("proceso").join("evaluador").get("nombres"),
                                root.join("proceso").join("evaluador").get("apellidos")),
                        "%" + nombreEvaluador + "%"));
            }

            // Filtro por roles
            if (roles != null && !roles.isEmpty()) {
                predicates.add(root.join("proceso").join("evaluador").join("roles").get("nombre").in(roles));
            }

            // Filtro combinado para tipoFuente y estadoFuente en las fuentes
            if (tipoFuente != null || estadoFuente != null) {
                var fuenteJoin = root.join("fuentes", JoinType.INNER);

                if (tipoFuente != null) {
                    predicates.add(builder.equal(fuenteJoin.get("tipoFuente"), tipoFuente));
                }
                if (estadoFuente != null) {
                    predicates.add(builder.equal(fuenteJoin.get("estadoFuente").get("nombreEstado"), estadoFuente));
                }
            }

            // Combina todos los predicados con "and"
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
