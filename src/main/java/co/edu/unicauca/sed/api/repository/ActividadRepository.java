package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Integer> {
}
