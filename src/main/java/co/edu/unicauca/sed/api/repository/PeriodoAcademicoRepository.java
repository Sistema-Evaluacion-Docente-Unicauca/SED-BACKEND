package co.edu.unicauca.sed.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unicauca.sed.api.model.PeriodoAcademico;

public interface PeriodoAcademicoRepository extends JpaRepository<PeriodoAcademico, Integer> {
  Optional<PeriodoAcademico> findByEstado(Integer estado);
  /**
     * Verifica si un período académico existe por su ID de período.
     *
     * @param idPeriodo El ID del período académico a buscar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByIdPeriodo(String idPeriodo);
}
