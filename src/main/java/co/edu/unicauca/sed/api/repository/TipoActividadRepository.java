package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.TipoActividad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoActividadRepository extends CrudRepository<TipoActividad, Integer> {
}
