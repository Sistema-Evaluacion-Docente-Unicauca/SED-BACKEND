package co.edu.unicauca.sed.api.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unicauca.sed.api.model.PeriodoAcademico;

public interface PeriodoAcademicoRepository extends CrudRepository<PeriodoAcademico, Integer> {
  Optional<PeriodoAcademico> findByEstado(Integer estado);
}
