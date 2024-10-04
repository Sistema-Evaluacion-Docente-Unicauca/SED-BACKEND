package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.EstadoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoActividadRepository extends JpaRepository<EstadoActividad, Integer> {
}
