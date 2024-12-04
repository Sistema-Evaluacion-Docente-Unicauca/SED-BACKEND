package co.edu.unicauca.sed.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.model.Usuario;

public interface ProcesoRepository extends JpaRepository<Proceso, Integer> {

    List<Proceso> findByEvaluado_OidUsuario(Integer oidUsuario);
    List<Proceso> findByEvaluado_OidUsuarioAndOidPeriodoAcademico_Estado(Integer oidUsuario, Integer estado);
    List<Proceso> findByOidPeriodoAcademico_OidPeriodoAcademico(Integer oidPeriodoAcademico);
    List<Proceso> findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademico(Usuario evaluado, Integer oidPeriodoAcademico);
}
