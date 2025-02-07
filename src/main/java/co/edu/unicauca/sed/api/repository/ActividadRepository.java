package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Actividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Integer>, JpaSpecificationExecutor<Actividad> {
    List<Actividad> findByProceso_Evaluado_OidUsuario(Integer oidUsuario);
    @Query("SELECT a FROM Actividad a WHERE a.proceso.oidProceso IN :procesoIds")
    Page<Actividad> findByProcesos(@Param("procesoIds") List<Integer> procesoIds, Pageable pageable);
}
