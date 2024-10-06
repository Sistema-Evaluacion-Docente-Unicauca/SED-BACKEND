package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Encuesta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncuestaRepository extends CrudRepository<Encuesta, Integer> {
}
