package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.UsuarioDetalle;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioDetalleRepository extends CrudRepository<UsuarioDetalle, Integer> {
}
