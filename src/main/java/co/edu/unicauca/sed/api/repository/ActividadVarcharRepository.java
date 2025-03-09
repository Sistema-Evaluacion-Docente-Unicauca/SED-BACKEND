package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.ActividadVarchar;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad ActividadVarchar.
 */
@Repository
public interface ActividadVarcharRepository extends JpaRepository<ActividadVarchar, Integer> {
    void deleteByActividad(Actividad actividad);
    List<ActividadVarchar> findByActividad(Actividad actividad);
}
