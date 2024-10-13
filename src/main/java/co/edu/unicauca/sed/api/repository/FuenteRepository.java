package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Fuente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface FuenteRepository extends CrudRepository<Fuente, Integer> {

    // Método personalizado para buscar fuentes por OID de la actividad
    @Query("SELECT f FROM Fuente f WHERE f.actividad.oidActividad = :oidActividad")
    List<Fuente> findByActividadOid(@Param("oidActividad") Integer oidActividad);
}
