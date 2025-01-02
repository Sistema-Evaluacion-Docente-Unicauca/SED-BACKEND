package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.EncuestaEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncuestaEstudianteRepository extends JpaRepository<EncuestaEstudiante, Integer> {
}
