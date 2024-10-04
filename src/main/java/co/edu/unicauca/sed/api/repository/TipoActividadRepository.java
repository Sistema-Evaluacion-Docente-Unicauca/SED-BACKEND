package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.TipoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoActividadRepository extends JpaRepository<TipoActividad, Integer> {
}
