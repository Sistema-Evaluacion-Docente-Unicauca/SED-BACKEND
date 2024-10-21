package co.edu.unicauca.sed.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicauca.sed.api.model.Proceso;

public interface ProcesoRepository extends JpaRepository<Proceso, Integer> {

    List<Proceso> findByEvaluado_OidUsuario(Integer oidUsuario);
    List<Proceso> findByEvaluado_OidUsuarioAndOidPeriodoAcademico_Estado(Integer oidUsuario, Integer estado);
}
