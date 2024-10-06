package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Autoevaluacion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoevaluacionRepository extends CrudRepository<Autoevaluacion, Integer> {
}
