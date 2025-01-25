package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Integer>, JpaSpecificationExecutor<Actividad> {
    // Method to get activities by evaluator
    List<Actividad> findByProceso_Evaluado_OidUsuario(Integer oidUsuario);}
