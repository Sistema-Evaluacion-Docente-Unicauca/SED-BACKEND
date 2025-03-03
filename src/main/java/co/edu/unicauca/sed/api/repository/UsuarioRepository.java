package co.edu.unicauca.sed.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import co.edu.unicauca.sed.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>, JpaSpecificationExecutor<Usuario> {
    Usuario findByIdentificacion(String identificacion);

    boolean existsByRolesNombre(String nombreRol);

    @Query("SELECT COUNT(u) FROM Usuario u JOIN u.roles r WHERE u.usuarioDetalle.departamento = :departamento AND r.nombre IN :roles AND u.oidUsuario <> :idUsuario")
    long countByUsuarioDetalle_DepartamentoAndRoles_NombreInExcludingUser(String departamento, List<String> roles,
            Integer idUsuario);

    @Query("SELECT COUNT(u) FROM Usuario u JOIN u.roles r WHERE u.usuarioDetalle.facultad = :facultad AND r.nombre IN :roles AND u.oidUsuario <> :idUsuario")
    long countByUsuarioDetalle_FacultadAndRoles_NombreInExcludingUser(String facultad, List<String> roles,
            Integer idUsuario);

    Optional<Usuario> findFirstByUsuarioDetalle_DepartamentoAndRoles_NombreIgnoreCase(String departamento,
            String nombreRol);
}
