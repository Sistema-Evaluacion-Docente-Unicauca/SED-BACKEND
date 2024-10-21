package co.edu.unicauca.sed.api.specification;

import co.edu.unicauca.sed.api.model.Actividad;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;

import java.util.List;

public class ActividadSpecification {

    public static Specification<Actividad> hasTipoActividad(String nombreTipoActividad) {
        return (root, query, criteriaBuilder) ->
                nombreTipoActividad == null ? null :
                criteriaBuilder.equal(root.join("tipoActividad").get("nombre"), nombreTipoActividad);
    }

    public static Specification<Actividad> hasNombreEvaluador(String nombreEvaluador) {
        return (root, query, criteriaBuilder) -> {
            if (nombreEvaluador == null) {
                return null;
            }
            // Join to Proceso and then to Usuario (Evaluador)
            return criteriaBuilder.like(criteriaBuilder.concat(
                root.join("proceso").join("evaluador").get("nombres"),
                root.join("proceso").join("evaluador").get("apellidos")
            ), "%" + nombreEvaluador + "%");
        };
    }

    public static Specification<Actividad> hasRoles(List<String> roles) {
        return (root, query, criteriaBuilder) -> {
            if (roles == null || roles.isEmpty()) {
                return null;
            }
            // Join to Proceso -> Evaluador -> Roles
            return root.join("proceso").join("evaluador").join("roles", JoinType.INNER).get("nombre").in(roles);
        };
    }

    public static Specification<Actividad> hasTipoFuente(String tipoFuente) {
        return (root, query, criteriaBuilder) -> {
            if (tipoFuente == null) {
                return null;
            }
            return criteriaBuilder.equal(
                root.join("fuentes", JoinType.INNER).get("tipoFuente"),
                tipoFuente
            );
        };
    }

    public static Specification<Actividad> hasEstadoFuente(String estadoFuente) {
        return (root, query, criteriaBuilder) -> {
            if (estadoFuente == null) {
                return null;
            }
            return criteriaBuilder.equal(
                root.join("fuentes", JoinType.INNER).join("oidestadofuente").get("nombreEstado"),
                estadoFuente
            );
        };
    }
}
