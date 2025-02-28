package co.edu.unicauca.sed.api.specification;

import co.edu.unicauca.sed.api.model.Consolidado;
import co.edu.unicauca.sed.api.service.PeriodoAcademicoService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ConsolidadoSpecification {

    private final PeriodoAcademicoService periodoAcademicoService;

    public ConsolidadoSpecification(PeriodoAcademicoService periodoAcademicoService) {
        this.periodoAcademicoService = periodoAcademicoService;
    }

    public Specification<Consolidado> byFilters(
            Integer idUsuario, String nombre, String identificacion,
            String facultad, String departamento, String categoria,
            Integer idPeriodoAcademico) {

        Specification<Consolidado> spec = Specification.where(null);

        if (idUsuario != null) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.equal(root.get("proceso").get("evaluado").get("oidUsuario"), idUsuario)
            );
        }

        if (StringUtils.hasText(nombre)) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.like(
                    criteriaBuilder.upper(criteriaBuilder.concat(
                        criteriaBuilder.concat(root.get("proceso").get("evaluado").get("nombres"), " "),
                        root.get("proceso").get("evaluado").get("apellidos")
                    )),
                    "%" + nombre.toUpperCase() + "%"
                )
            );
        }

        if (StringUtils.hasText(identificacion)) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.like(root.get("proceso").get("evaluado").get("identificacion"),
                    "%" + identificacion + "%")
            );
        }

        if (StringUtils.hasText(facultad)) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.like(root.get("proceso").get("evaluado").get("usuarioDetalle").get("facultad"),
                    "%" + facultad + "%")
            );
        }

        if (StringUtils.hasText(departamento)) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.like(root.get("proceso").get("evaluado").get("usuarioDetalle").get("departamento"),
                    "%" + departamento + "%")
            );
        }

        if (StringUtils.hasText(categoria)) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.like(root.get("proceso").get("evaluado").get("usuarioDetalle").get("categoria"),
                    "%" + categoria + "%")
            );
        }

        if (idPeriodoAcademico != null) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.equal(root.get("proceso").get("oidPeriodoAcademico").get("oidPeriodoAcademico"), idPeriodoAcademico)
            );
        }

        return spec;
    }
}
