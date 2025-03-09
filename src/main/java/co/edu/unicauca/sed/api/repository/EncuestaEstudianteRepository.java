package co.edu.unicauca.sed.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unicauca.sed.api.domain.EncuestaEstudiante;

@Repository
public interface EncuestaEstudianteRepository extends JpaRepository<EncuestaEstudiante, Integer> {
}
