package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.EstadoFuente;
import co.edu.unicauca.sed.api.service.EstadoFuenteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        try {
            Page<EstadoFuente> estadoFuentePage = estadoFuenteService.findAll(pageable);
            logger.info("Listando registros de EstadoFuente, total: {}", estadoFuentePage.getTotalElements());
            return ResponseEntity.ok(estadoFuentePage);
        } catch (Exception e) {
            logger.error("Error al listar los registros de EstadoFuente", e);
            throw new RuntimeException("Error al listar los registros de EstadoFuente.");
        }
    }

    /**
     * Obtener EstadoFuente por ID.
     *
     * @param id ID del EstadoFuente.
     * @return EstadoFuente encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EstadoFuente> getById(@PathVariable Integer id) {
        try {
            EstadoFuente estadoFuente = estadoFuenteService.findById(id);
            logger.info("Se obtuvo el EstadoFuente con ID: {}", id);
            return ResponseEntity.ok(estadoFuente);
        } catch (Exception e) {
            logger.error("Error al obtener el EstadoFuente con ID: {}", id, e);
            throw new RuntimeException("Error al obtener el EstadoFuente.");
        }
    }

    /**
     * Crear o actualizar un EstadoFuente.
     *
     * @param estadoFuente Objeto EstadoFuente.
     * @return EstadoFuente guardado.
     */
    @PostMapping
    public ResponseEntity<EstadoFuente> save(@RequestBody EstadoFuente estadoFuente) {
        try {
            EstadoFuente savedEstadoFuente = estadoFuenteService.save(estadoFuente);
            logger.info("Se guardó el EstadoFuente con ID: {}", savedEstadoFuente.getOidEstadoFuente());
            return ResponseEntity.ok(savedEstadoFuente);
        } catch (Exception e) {
            logger.error("Error al guardar el EstadoFuente: {}", estadoFuente, e);
            throw new RuntimeException("Error al guardar el EstadoFuente.");
        }
    }

    /**
     * Eliminar un EstadoFuente por ID.
     *
     * @param id ID del EstadoFuente a eliminar.
     * @return Respuesta de éxito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            estadoFuenteService.deleteById(id);
            logger.info("Se eliminó el EstadoFuente con ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error al eliminar el EstadoFuente con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar el EstadoFuente.");
        }
    }
}
