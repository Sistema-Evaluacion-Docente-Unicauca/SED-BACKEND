package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Actividad;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActividadRepository extends CrudRepository<Actividad, Integer> {
    List<Actividad> findAll();
}
