package co.edu.unicauca.sed.api.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import co.edu.unicauca.sed.api.model.Comentario;
import co.edu.unicauca.sed.api.repository.ComentarioRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Servicio para gestionar las operaciones relacionadas con los comentarios.
 */
@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    /**
     * Recupera todos los comentarios con paginación y ordenamiento por fecha de creación.
     *
     * @param pageable       Objeto Pageable para controlar la paginación.
     * @param ascendingOrder Indica si los resultados deben ordenarse en orden ascendente (true) o descendente (false).
     * @return Página de comentarios ordenados y paginados.
     */
    public Page<Comentario> findAll(Pageable pageable, Boolean ascendingOrder) {
        boolean order = (ascendingOrder != null) ? ascendingOrder : true;
        Sort sort = order ? Sort.by("fechaCreacion").ascending() : Sort.by("fechaCreacion").descending();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return comentarioRepository.findAll(sortedPageable);
    }

    /**
     * Busca un comentario por su ID.
     *
     * @param oid ID del comentario.
     * @return Comentario encontrado, o null si no existe.
     */
    public Comentario findByOid(Integer oid) {
        Optional<Comentario> resultado = this.comentarioRepository.findById(oid);

        if (resultado.isPresent()) {
            return resultado.get();
        }

        return null;
    }

    /**
     * Guarda un nuevo comentario o actualiza uno existente.
     *
     * @param comentario Objeto Comentario a guardar.
     * @return Comentario guardado, o null si ocurre un error.
     */
    public Comentario save(Comentario comentario) {
        Comentario result = null;
        try {
            if (comentario.getComentario() != null) {
                comentario.setComentario(comentario.getComentario().toUpperCase());
            }
            result = this.comentarioRepository.save(comentario);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     * Elimina un comentario por su ID.
     *
     * @param oid ID del comentario a eliminar.
     */
    public void delete(Integer oid) {
        this.comentarioRepository.deleteById(oid);
    }
}
