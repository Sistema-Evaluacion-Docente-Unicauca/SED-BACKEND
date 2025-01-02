package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.EvaluacionEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluacionEstudianteRepository extends JpaRepository<EvaluacionEstudiante, Integer> {
}
