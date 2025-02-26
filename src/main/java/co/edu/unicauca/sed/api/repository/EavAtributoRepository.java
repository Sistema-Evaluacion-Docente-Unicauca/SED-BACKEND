package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.EavAtributo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad EavAtributo.
 */
@Repository
public interface EavAtributoRepository extends JpaRepository<EavAtributo, Integer> {
    Optional<EavAtributo> findByNombre(String nombre); 
}
