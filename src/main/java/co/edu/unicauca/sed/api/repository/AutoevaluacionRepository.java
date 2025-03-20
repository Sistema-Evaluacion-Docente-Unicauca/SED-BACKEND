package co.edu.unicauca.sed.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import co.edu.unicauca.sed.api.domain.Autoevaluacion;

@Repository
public interface AutoevaluacionRepository extends JpaRepository<Autoevaluacion, Integer>, JpaSpecificationExecutor<Autoevaluacion> {

}