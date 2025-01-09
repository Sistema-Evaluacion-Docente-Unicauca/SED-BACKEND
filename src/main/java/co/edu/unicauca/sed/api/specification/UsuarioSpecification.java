package co.edu.unicauca.sed.api.specification;

import co.edu.unicauca.sed.api.model.Usuario;
import org.springframework.data.jpa.domain.Specification;

public class UsuarioSpecification {

    public static Specification<Usuario> byFilters(
            String identificacion, String nombre, String facultad, String departamento, String categoria,
            String contratacion, String dedicacion, String estudios, String rol, Short estado) {

        Specification<Usuario> spec = Specification.where(null);

        if (identificacion != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("identificacion"), identificacion));
        }
        
        if (nombre != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.upper(
                    criteriaBuilder.concat(
                        criteriaBuilder.concat(root.get("nombres"), " "),
                        root.get("apellidos")
                    )
                ),
                "%" + nombre.toUpperCase() + "%"
            ));
        }
            

        if (facultad != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("facultad"), facultad));
        }

        if (departamento != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("departamento"), departamento));
        }

        if (categoria != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("categoria"), categoria));
        }

        if (contratacion != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("contratacion"), contratacion));
        }

        if (dedicacion != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("dedicacion"), dedicacion));
        }

        if (estudios != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("estudios"), estudios));
        }

        if (rol != null) {
            spec = spec.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("roles").get("nombre"), rol));
        }

        if (estado != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("estado"), estado));
        }

        return spec;
    }
}
