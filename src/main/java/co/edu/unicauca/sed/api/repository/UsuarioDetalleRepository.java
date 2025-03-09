package co.edu.unicauca.sed.api.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.edu.unicauca.sed.api.domain.UsuarioDetalle;

@Repository
public interface UsuarioDetalleRepository extends JpaRepository<UsuarioDetalle, Integer> {

  // Métodos para UsuarioDetalle
  @Query("SELECT DISTINCT u.facultad FROM UsuarioDetalle u")
  List<String> findDistinctFacultad();

  @Query("SELECT DISTINCT u.departamento FROM UsuarioDetalle u")
  List<String> findDistinctDepartamento();

  @Query("SELECT DISTINCT u.categoria FROM UsuarioDetalle u")
  List<String> findDistinctCategoria();

  @Query("SELECT DISTINCT u.contratacion FROM UsuarioDetalle u")
  List<String> findDistinctContratacion();

  @Query("SELECT DISTINCT u.dedicacion FROM UsuarioDetalle u")
  List<String> findDistinctDedicacion();

  @Query("SELECT DISTINCT u.estudios FROM UsuarioDetalle u")
  List<String> findDistinctEstudios();
}
