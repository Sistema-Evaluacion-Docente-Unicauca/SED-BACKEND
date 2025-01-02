package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.UsuarioDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioDetalleRepository extends JpaRepository<UsuarioDetalle, Integer> {
  UsuarioDetalle findByIdentificacion(String identificacion);
}
