package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.EncuestaEstudiante;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncuestaEstudianteRepository extends CrudRepository<EncuestaEstudiante, Integer> {
}
