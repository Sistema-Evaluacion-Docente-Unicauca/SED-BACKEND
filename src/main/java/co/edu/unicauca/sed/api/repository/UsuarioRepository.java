package co.edu.unicauca.sed.api.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import co.edu.unicauca.sed.api.model.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {
  List<Usuario> findByRoles_Nombre(String nombreRol);
}
