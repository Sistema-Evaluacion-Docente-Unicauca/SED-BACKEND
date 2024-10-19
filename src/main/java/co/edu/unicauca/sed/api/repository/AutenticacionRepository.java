package co.edu.unicauca.sed.api.repository;

import co.edu.unicauca.sed.api.model.Autenticacion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutenticacionRepository extends CrudRepository<Autenticacion, Integer> {
    // Puedes agregar métodos de búsqueda personalizados si los necesitas
}
