package co.edu.unicauca.sed.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import co.edu.unicauca.sed.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>, JpaSpecificationExecutor<Usuario> {
    Usuario findByIdentificacion(String identificacion);
    boolean existsByRolesNombre(String nombreRol);
    Long countByUsuarioDetalle_DepartamentoAndRoles_NombreIn(String departamento, List<String> roles);
    Long countByUsuarioDetalle_FacultadAndRoles_NombreIn(String facultad, List<String> roles);
}
