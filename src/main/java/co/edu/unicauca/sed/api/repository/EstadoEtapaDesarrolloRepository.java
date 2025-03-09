package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.EstadoEtapaDesarrollo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad EstadoEtapaDesarrollo.
 */
@Repository
public interface EstadoEtapaDesarrolloRepository extends JpaRepository<EstadoEtapaDesarrollo, Integer> {
}
