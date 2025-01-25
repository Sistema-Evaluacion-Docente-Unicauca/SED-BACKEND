package co.edu.unicauca.sed.api.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import co.edu.unicauca.sed.api.model.Proceso;
import co.edu.unicauca.sed.api.model.Usuario;

public interface ProcesoRepository extends JpaRepository<Proceso, Integer>, JpaSpecificationExecutor<Proceso> {

    List<Proceso> findByEvaluado_OidUsuario(Integer oidUsuario);
    List<Proceso> findByOidPeriodoAcademico_OidPeriodoAcademico(Integer oidPeriodoAcademico);
    List<Proceso> findByEvaluadoAndOidPeriodoAcademico_OidPeriodoAcademico(Usuario evaluado, Integer oidPeriodoAcademico);
    List<Proceso> findByEvaluado_OidUsuarioAndOidPeriodoAcademico_OidPeriodoAcademico(Integer oidUsuario, Integer oidPeriodoAcademico);
    /**
     * Encuentra todos los procesos asociados a un evaluado específico.
     *
     * @param evaluado El evaluado cuyo procesos se desean buscar.
     * @return Lista de procesos asociados al evaluado.
     */
    List<Proceso> findByEvaluado(Usuario evaluado);
}
