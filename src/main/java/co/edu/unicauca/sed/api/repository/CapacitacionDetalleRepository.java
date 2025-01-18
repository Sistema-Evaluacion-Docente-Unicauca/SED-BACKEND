package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.CapacitacionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapacitacionDetalleRepository extends JpaRepository<CapacitacionDetalle, Integer> {
}
