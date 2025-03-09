package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.domain.EstadoFuente;
import co.edu.unicauca.sed.api.dto.ApiResponse;
import co.edu.unicauca.sed.api.service.EstadoFuenteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estado-fuente")
public class EstadoFuenteController {

    @Autowired
    private EstadoFuenteService estadoFuenteService;

    private static final Logger logger = LoggerFactory.getLogger(EstadoFuenteController.class);

    /**
     * Listar todos los registros de EstadoFuente con paginación.
     *
     * @param pageable objeto de paginación.
     * @return Página de EstadoFuente.
     */
    @GetMapping
    public ResponseEntity<Page<EstadoFuente>> getAll(Pageable pageable) {
        return ResponseEntity.ok(estadoFuenteService.findAll(pageable));
    }

    /**
     * Obtener EstadoFuente por ID.
     *
     * @param id ID del EstadoFuente.
     * @return EstadoFuente encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EstadoFuente>> getById(@PathVariable Integer id) {
        try {
            EstadoFuente estadoFuente = estadoFuenteService.findById(id);
            logger.info("✅ [GET] EstadoFuente obtenido con ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>(200, "EstadoFuente encontrado.", estadoFuente));
        } catch (Exception e) {
            logger.error("❌ [ERROR] No se pudo obtener EstadoFuente con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "EstadoFuente no encontrado con ID: " + id, null));
        }
    }

    /**
     * Guardar un EstadoFuente.
     *
     * @param estadoFuente Objeto EstadoFuente.
     * @return EstadoFuente guardado.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EstadoFuente>> save(@RequestBody EstadoFuente estadoFuente) {
        return estadoFuenteService.save(estadoFuente);
    }

    /**
     * Actualizar un EstadoFuente existente.
     *
     * @param id ID del EstadoFuente a actualizar.
     * @param estadoFuente Objeto EstadoFuente con nuevos valores.
     * @return EstadoFuente actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EstadoFuente>> update(@PathVariable Integer id, @RequestBody EstadoFuente estadoFuente) {
        return estadoFuenteService.update(id, estadoFuente);
    }

    /**
     * Eliminar un EstadoFuente por ID.
     *
     * @param id ID del EstadoFuente a eliminar.
     * @return Respuesta de éxito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        return estadoFuenteService.deleteById(id);
    }
}
