package co.edu.unicauca.sed.api.specification;

import co.edu.unicauca.sed.api.model.Usuario;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UsuarioSpecification {

    public static Specification<Usuario> byFilters(
            String identificacion, String nombre, String facultad, String departamento, String categoria,
            String contratacion, String dedicacion, String estudios, String rol, Short estado) {

        Specification<Usuario> spec = Specification.where(null);

        if (StringUtils.hasText(identificacion)) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("identificacion"), identificacion));
        }

        if (StringUtils.hasText(nombre)) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(
                    criteriaBuilder.upper(
                            criteriaBuilder.concat(
                                    criteriaBuilder.concat(root.get("nombres"), " "),
                                    root.get("apellidos"))),
                    "%" + nombre.toUpperCase() + "%"));
        }

        if (StringUtils.hasText(facultad)) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("facultad"), facultad));
        }

        if (StringUtils.hasText(departamento)) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("departamento"), departamento));
        }

        if (StringUtils.hasText(categoria)) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("categoria"), categoria));
        }

        if (StringUtils.hasText(contratacion)) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("contratacion"), contratacion));
        }

        if (StringUtils.hasText(dedicacion)) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("dedicacion"), dedicacion));
        }

        if (StringUtils.hasText(estudios)) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("usuarioDetalle").get("estudios"), estudios));
        }

        if (StringUtils.hasText(rol)) {
            spec = spec.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("roles").get("nombre"), rol));
        }

        if (estado != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("estadoUsuario").get("nombre"), estado));
        }

        return spec;
    }
}
