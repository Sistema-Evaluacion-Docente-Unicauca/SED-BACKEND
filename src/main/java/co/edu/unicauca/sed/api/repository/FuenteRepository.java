package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Fuente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuenteRepository extends CrudRepository<Fuente, Integer> {
}
