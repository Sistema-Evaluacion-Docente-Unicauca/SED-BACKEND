package co.edu.unicauca.sed.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unicauca.sed.api.model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

}
