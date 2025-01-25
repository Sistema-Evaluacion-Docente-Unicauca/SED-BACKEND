package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.EstadoFuente;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoFuenteRepository extends JpaRepository<EstadoFuente, Integer> {
    Optional<EstadoFuente> findByNombreEstado(String nombreEstado);
}
