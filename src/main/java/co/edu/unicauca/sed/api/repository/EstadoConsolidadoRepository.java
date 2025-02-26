package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.EstadoConsolidado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad EstadoConsolidado.
 */
@Repository
public interface EstadoConsolidadoRepository extends JpaRepository<EstadoConsolidado, Integer> {
}
