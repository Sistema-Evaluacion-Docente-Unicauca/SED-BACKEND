package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreguntaRepository extends JpaRepository<Pregunta, Integer> {
}
