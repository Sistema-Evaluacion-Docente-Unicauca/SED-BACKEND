package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.ExtensionDetalle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtensionDetalleRepository extends JpaRepository<ExtensionDetalle, Integer> {
    Optional<ExtensionDetalle> findByActividadOidActividad(Integer oidActividad);
}
