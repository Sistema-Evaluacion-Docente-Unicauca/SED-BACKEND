package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Encuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncuestaRepository extends JpaRepository<Encuesta, Integer> {
}
