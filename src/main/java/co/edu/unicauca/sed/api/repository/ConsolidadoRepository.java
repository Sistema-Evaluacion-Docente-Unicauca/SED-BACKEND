package co.edu.unicauca.sed.api.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import co.edu.unicauca.sed.api.model.Consolidado;
import co.edu.unicauca.sed.api.model.Proceso;

public interface ConsolidadoRepository extends CrudRepository<Consolidado, Integer> {
  Optional<Consolidado> findByProceso(Proceso proceso);
}
