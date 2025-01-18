package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.TrabajoDocenciaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrabajoDocenciaDetalleRepository extends JpaRepository<TrabajoDocenciaDetalle, Integer> {
}
