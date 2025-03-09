package co.edu.unicauca.sed.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unicauca.sed.api.model.Consolidado;
import co.edu.unicauca.sed.api.model.Proceso;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsolidadoRepository extends JpaRepository<Consolidado, Integer>, JpaSpecificationExecutor<Consolidado> {
  Optional<Consolidado> findByProceso(Proceso proceso);
}
