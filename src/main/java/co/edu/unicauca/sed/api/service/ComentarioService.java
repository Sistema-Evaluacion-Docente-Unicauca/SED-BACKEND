package co.edu.unicauca.sed.api.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import co.edu.unicauca.sed.api.model.Comentario;
import co.edu.unicauca.sed.api.repository.ComentarioRepository;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio para gestionar las operaciones relacionadas con los comentarios.
 */
@Service
public class ComentarioService {

    private static final Logger logger = LoggerFactory.getLogger(ComentarioService.class);

    @Autowired
    private ComentarioRepository comentarioRepository;

    /**
     * Recupera todos los comentarios con paginación y ordenamiento por fecha de creación.
     */
    public ApiResponse<Page<Comentario>> findAll(Pageable pageable, Boolean ascendingOrder) {
        try {
            boolean order = (ascendingOrder != null) ? ascendingOrder : true;
            Sort sort = order ? Sort.by("fechaCreacion").ascending() : Sort.by("fechaCreacion").descending();
            Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            Page<Comentario> comentarios = comentarioRepository.findAll(sortedPageable);

            if (comentarios.isEmpty()) {
                return new ApiResponse<>(404, "No se encontraron comentarios.", Page.empty());
            }

            return new ApiResponse<>(200, "Comentarios obtenidos correctamente.", comentarios);

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al obtener los comentarios: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al obtener los comentarios.", Page.empty());
        }
    }

    /**
     * Busca un comentario por su ID.
     */
    public ApiResponse<Comentario> findByOid(Integer oid) {
        try {
            Optional<Comentario> resultado = comentarioRepository.findById(oid);
            if (resultado.isEmpty()) {
                return new ApiResponse<>(404, "Comentario con ID " + oid + " no encontrado.", null);
            }
            return new ApiResponse<>(200, "Comentario encontrado correctamente.", resultado.get());

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al buscar comentario por ID: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al buscar el comentario.", null);
        }
    }

    /**
     * Guarda un nuevo comentario o actualiza uno existente.
     */
    public ApiResponse<Comentario> save(Comentario comentario) {
        try {
            if (comentario == null || comentario.getComentario() == null || comentario.getComentario().isBlank()) {
                return new ApiResponse<>(400, "Error: El comentario no puede estar vacío.", null);
            }

            comentario.setComentario(comentario.getComentario().toUpperCase());
            Comentario savedComentario = comentarioRepository.save(comentario);
            logger.info("✅ [SAVE] Comentario guardado con ID: {}", savedComentario.getOidComentario());

            return new ApiResponse<>(201, "Comentario guardado correctamente.", savedComentario);

        } catch (DataIntegrityViolationException e) {
            logger.error("❌ [ERROR] Restricción de integridad violada al guardar comentario: {}", e.getMessage());
            return new ApiResponse<>(409, "Error: Datos inválidos o duplicados.", null);
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error inesperado al guardar el comentario: {}", e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al guardar el comentario.", null);
        }
    }

    /**
     * Elimina un comentario por su ID.
     */
    public ApiResponse<Void> delete(Integer oid) {
        try {
            if (!comentarioRepository.existsById(oid)) {
                return new ApiResponse<>(404, "Comentario con ID " + oid + " no encontrado.", null);
            }

            comentarioRepository.deleteById(oid);
            logger.info("✅ [DELETE] Comentario eliminado con ID: {}", oid);
            return new ApiResponse<>(200, "Comentario eliminado correctamente.", null);

        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al eliminar comentario con ID {}: {}", oid, e.getMessage(), e);
            return new ApiResponse<>(500, "Error inesperado al eliminar el comentario.", null);
        }
    }
}
