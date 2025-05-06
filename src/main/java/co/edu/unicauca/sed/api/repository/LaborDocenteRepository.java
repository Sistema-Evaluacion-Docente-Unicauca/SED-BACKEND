package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.domain.LaborDocente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaborDocenteRepository extends JpaRepository<LaborDocente, Integer> {
    Optional<LaborDocente> findByUsuarioOidUsuario(Integer oidUsuario);
}