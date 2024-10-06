package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.EvaluacionEstudiante;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluacionEstudianteRepository extends CrudRepository<EvaluacionEstudiante, Integer> {
}
