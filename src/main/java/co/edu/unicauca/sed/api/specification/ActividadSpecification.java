package co.edu.unicauca.sed.api.specification;

import org.springframework.data.jpa.domain.Specification;
import co.edu.unicauca.sed.api.model.Actividad;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ActividadSpecification {

    // Constantes para los nombres de los atributos
    private static final String ATTR_TIPO_ACTIVIDAD = "tipoActividad";
    private static final String ATTR_NOMBRE = "nombre";
    private static final String ATTR_PROCESO = "proceso";
    private static final String ATTR_EVALUADOR = "evaluador";
    private static final String ATTR_NOMBRES = "nombres";
    private static final String ATTR_APELLIDOS = "apellidos";
    private static final String ATTR_ROLES = "roles";
    private static final String ATTR_NOMBRE_ROL = "nombre";
    private static final String ATTR_FUENTES = "fuentes";
    private static final String ATTR_TIPO_FUENTE = "tipoFuente";
    private static final String ATTR_ESTADO_FUENTE = "estadoFuente";
    private static final String ATTR_NOMBRE_ESTADO = "nombreEstado";

    public static Specification<Actividad> hasFilters(String tipoActividad, String nombreEvaluador, List<String> roles, String tipoFuente, String estadoFuente) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por tipoActividad
            if (tipoActividad != null) {
                predicates.add(builder.equal(root.get(ATTR_TIPO_ACTIVIDAD).get(ATTR_NOMBRE), tipoActividad));
            }

            // Filtro por nombreEvaluador
            if (nombreEvaluador != null) {
                predicates.add(builder.like(
                        builder.concat(root.join(ATTR_PROCESO).join(ATTR_EVALUADOR).get(ATTR_NOMBRES),
                                root.join(ATTR_PROCESO).join(ATTR_EVALUADOR).get(ATTR_APELLIDOS)),
                        "%" + nombreEvaluador + "%"));
            }

            // Filtro por roles
            if (roles != null && !roles.isEmpty()) {
                predicates.add(root.join(ATTR_PROCESO).join(ATTR_EVALUADOR).join(ATTR_ROLES).get(ATTR_NOMBRE_ROL).in(roles));
            }

            // Filtro combinado para tipoFuente y estadoFuente en las fuentes
            if (tipoFuente != null || estadoFuente != null) {
                var fuenteJoin = root.join(ATTR_FUENTES, JoinType.INNER);

                if (tipoFuente != null) {
                    predicates.add(builder.equal(fuenteJoin.get(ATTR_TIPO_FUENTE), tipoFuente));
                }
                if (estadoFuente != null) {
                    predicates.add(builder.equal(fuenteJoin.get(ATTR_ESTADO_FUENTE).get(ATTR_NOMBRE_ESTADO), estadoFuente));
                }
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
