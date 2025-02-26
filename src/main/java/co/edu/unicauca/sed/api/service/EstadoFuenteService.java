package co.edu.unicauca.sed.api.service;

import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.model.EstadoFuente;
import co.edu.unicauca.sed.api.repository.EstadoFuenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EstadoFuenteService {

    private static final Logger logger = LoggerFactory.getLogger(EstadoFuenteService.class);

    @Autowired
    private EstadoFuenteRepository estadoFuenteRepository;

    /**
     * Listar todos los registros de EstadoFuente con paginación.
     *
     * @param pageable objeto de paginación
     * @return Página de EstadoFuente
     */
    @Transactional(readOnly = true)
    public Page<EstadoFuente> findAll(Pageable pageable) {
        return estadoFuenteRepository.findAll(pageable);
    }

    /**
     * Buscar EstadoFuente por ID.
     *
     * @param id ID del EstadoFuente.
     * @return Objeto EstadoFuente o excepción si no se encuentra.
     */
    @Transactional(readOnly = true)
    public EstadoFuente findById(Integer id) {
        return estadoFuenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EstadoFuente no encontrado con ID: " + id));
    }

    /**
     * Guardar un nuevo EstadoFuente, convirtiendo el nombre a mayúsculas.
     *
     * @param estadoFuente objeto EstadoFuente.
     * @return EstadoFuente guardado.
     */
    @Transactional
    public ResponseEntity<ApiResponse<EstadoFuente>> save(EstadoFuente estadoFuente) {
        try {
            estadoFuente.setNombreEstado(estadoFuente.getNombreEstado().toUpperCase());
            EstadoFuente savedEstadoFuente = estadoFuenteRepository.save(estadoFuente);
            logger.info("✅ [SAVE] EstadoFuente guardado con ID: {}", savedEstadoFuente.getOidEstadoFuente());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "EstadoFuente guardado exitosamente.", savedEstadoFuente));
        } catch (Exception e) {
            logger.error("❌ [ERROR] Error al guardar EstadoFuente: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error al guardar el EstadoFuente: " + e.getMessage(), null));
        }
    }

    /**
     * Actualizar un EstadoFuente existente.
     *
     * @param id ID del EstadoFuente a actualizar.
     * @param estadoFuente objeto EstadoFuente con nuevos valores.
     * @return EstadoFuente actualizado.
     */
    @Transactional
    public ResponseEntity<ApiResponse<EstadoFuente>> update(Integer id, EstadoFuente estadoFuente) {
        try {
            // 🔹 Validar si el estado existe
            EstadoFuente existingEstadoFuente = estadoFuenteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("EstadoFuente no encontrado con ID: " + id));
    
            // 🔹 Validar que `nombreEstado` no sea nulo o vacío
            if (estadoFuente.getNombreEstado() == null || estadoFuente.getNombreEstado().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "El nombre del EstadoFuente no puede estar vacío.", null));
            }
    
            // 🔹 Convertir el nombre a mayúsculas antes de actualizar
            existingEstadoFuente.setNombreEstado(estadoFuente.getNombreEstado().toUpperCase());
            EstadoFuente updatedEstadoFuente = estadoFuenteRepository.save(existingEstadoFuente);
    
            logger.info("✅ [UPDATE] EstadoFuente actualizado con ID: {}", updatedEstadoFuente.getOidEstadoFuente());
            return ResponseEntity.ok(new ApiResponse<>(200, "EstadoFuente actualizado exitosamente.", updatedEstadoFuente));
    
        } catch (IllegalArgumentException e) {
            // 🔹 Capturar cuando el ID no existe y retornar 404
            logger.warn("⚠️ [UPDATE] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
    
        } catch (Exception e) {
            // 🔹 Capturar otros errores y registrar en logs
            logger.error("❌ [ERROR] Error al actualizar EstadoFuente: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error al actualizar el EstadoFuente: " + e.getMessage(), null));
        }
    }
    

    /**
     * Eliminar un EstadoFuente por ID.
     *
     * @param id ID del EstadoFuente a eliminar.
     */
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteById(Integer id) {
        if (!estadoFuenteRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "EstadoFuente no encontrado con ID: " + id, null));
        }
        estadoFuenteRepository.deleteById(id);
        logger.info("✅ [DELETE] EstadoFuente eliminado con ID: {}", id);
        return ResponseEntity.ok(new ApiResponse<>(200, "EstadoFuente eliminado exitosamente.", null));
    }

    /**
     * Crea una nueva instancia de `EstadoFuente` con el ID especificado.
     *
     * @param oidEstado ID del estado fuente.
     * @return Instancia de EstadoFuente.
     */
    public EstadoFuente createEstadoFuente(int oidEstado) {
        EstadoFuente stateSource = new EstadoFuente();
        stateSource.setOidEstadoFuente(oidEstado);
        return stateSource;
    }
}
