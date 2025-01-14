package co.edu.unicauca.sed.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unicauca.sed.api.model.Consolidado;
import co.edu.unicauca.sed.api.model.Proceso;

public interface ConsolidadoRepository extends JpaRepository<Consolidado, Integer> {
  Optional<Consolidado> findByProceso(Proceso proceso);
}
