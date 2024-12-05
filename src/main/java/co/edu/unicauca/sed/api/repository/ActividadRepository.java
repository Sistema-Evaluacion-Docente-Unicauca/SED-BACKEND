package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Actividad;
import co.edu.unicauca.sed.api.model.Proceso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Integer>, JpaSpecificationExecutor<Actividad> {

    // Method to get activities by evaluator
    List<Actividad> findByProceso_Evaluado_OidUsuario(Integer oidUsuario);

    // Method to get activities by evaluato where the academic period is active
    List<Actividad> findByProceso_Evaluado_OidUsuarioAndProceso_OidPeriodoAcademico_Estado(Integer oidUsuario, Integer estado);

    // Method to get activities by evaluator where the academic period is active
    List<Actividad> findByProceso_Evaluador_OidUsuarioAndProceso_OidPeriodoAcademico_Estado(Integer oidUsuario, Integer estado);

    // Method to get activities where the academic period of the process is active (state = 1)
    List<Actividad> findByProceso_OidPeriodoAcademico_Estado(Integer estado);

    List<Actividad> findByProceso_Evaluado_OidUsuarioAndProceso_OidPeriodoAcademico_OidPeriodoAcademico(Integer oidUsuario, Integer oidPeriodoAcademico);
}
