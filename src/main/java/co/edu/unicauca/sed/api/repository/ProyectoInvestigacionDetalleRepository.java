package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.ProyectoInvestigacionDetalle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProyectoInvestigacionDetalleRepository extends JpaRepository<ProyectoInvestigacionDetalle, Integer> {
    Optional<ProyectoInvestigacionDetalle> findByActividadOidActividad(Integer oidActividad);
}
