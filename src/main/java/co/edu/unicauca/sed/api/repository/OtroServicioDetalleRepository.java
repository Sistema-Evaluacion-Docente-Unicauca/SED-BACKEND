package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.OtroServicioDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtroServicioDetalleRepository extends JpaRepository<OtroServicioDetalle, Integer> {
}
