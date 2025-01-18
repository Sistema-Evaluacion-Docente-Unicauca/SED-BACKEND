package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.ExtensionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtensionDetalleRepository extends JpaRepository<ExtensionDetalle, Integer> {
}
