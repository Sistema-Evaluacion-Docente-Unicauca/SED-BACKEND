package co.edu.unicauca.sed.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import co.edu.unicauca.sed.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>, JpaSpecificationExecutor<Usuario> {
    Usuario findByIdentificacion(String identificacion);
    boolean existsByRolesNombre(String nombreRol);
}
