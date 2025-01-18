package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.DocenciaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocenciaDetalleRepository extends JpaRepository<DocenciaDetalle, Integer> {
}
