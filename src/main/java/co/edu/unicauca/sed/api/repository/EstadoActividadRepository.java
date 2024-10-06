package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.EstadoActividad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoActividadRepository extends CrudRepository<EstadoActividad, Integer> {
}
