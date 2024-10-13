package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Actividad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActividadRepository extends CrudRepository<Actividad, Integer> {

    @Query("SELECT a FROM Actividad a JOIN FETCH a.fuentes WHERE a.oidActividad = :oid")
    Optional<Actividad> findByOidWithFuentes(@Param("oid") Integer oid);

    @Query("SELECT a FROM Actividad a LEFT JOIN FETCH a.fuentes")
    List<Actividad> findAllWithFuentes();
}
