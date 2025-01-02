package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import co.edu.unicauca.sed.api.model.Encuesta;
import co.edu.unicauca.sed.api.service.EncuestaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para la gestión de encuestas.
 * Proporciona endpoints para realizar operaciones CRUD sobre las encuestas.
 */
@Controller
@RequestMapping("encuesta")
public class EncuestaController {

    private static final Logger logger = LoggerFactory.getLogger(EncuestaController.class);

    @Autowired
    private EncuestaService encuestaService;

    /**
     * Recupera todas las encuestas disponibles con paginación y ordenamiento.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param ascendingOrder Indica si el orden es ascendente.
     * @return Página de encuestas.
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Solicitud para recuperar todas las encuestas con paginación.");
        try {
            Page<Encuesta> encuestas = encuestaService.findAll(PageRequest.of(page, size));
            if (encuestas.hasContent()) {
                logger.info("Se recuperaron {} encuestas exitosamente.", encuestas.getTotalElements());
                return ResponseEntity.ok().body(encuestas);
            } else {
                logger.warn("No se encontraron encuestas.");
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            logger.error("Error al recuperar las encuestas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Recupera una encuesta específica por su ID.
     *
     * @param oid ID de la encuesta.
     * @return La encuesta encontrada o un mensaje de error si no existe.
     */
    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        logger.info("Solicitud para buscar la encuesta con ID: {}", oid);
        Encuesta encuesta = encuestaService.findByOid(oid);
        if (encuesta != null) {
            logger.info("Encuesta con ID {} encontrada exitosamente.", oid);
            return ResponseEntity.ok().body(encuesta);
        }
        logger.warn("Encuesta con ID {} no encontrada.", oid);
        return ResponseEntity.notFound().build();
    }

    /**
     * Guarda una nueva encuesta.
     *
     * @param encuesta Objeto Encuesta a guardar.
     * @return La encuesta guardada o un mensaje de error si ocurre algún problema.
     */
    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Encuesta encuesta) {
        try {
            Encuesta resultado = encuestaService.save(encuesta);
            if (resultado != null) {
                logger.info("Encuesta guardada exitosamente con ID: {}", resultado.getOidEncuesta());
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            logger.error("Error al guardar la encuesta: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        logger.error("El guardado de la encuesta devolvió un resultado nulo.");
        return ResponseEntity.internalServerError().body("Error: Resultado nulo.");
    }

    /**
     * Elimina una encuesta por su ID.
     *
     * @param oid ID de la encuesta a eliminar.
     * @return Confirmación de eliminación o un mensaje de error si ocurre un
     *         problema.
     */
    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        logger.info("Solicitud para eliminar la encuesta con ID: {}", oid);
        Encuesta encuesta = null;
        try {
            encuesta = encuestaService.findByOid(oid);
            if (encuesta == null) {
                logger.warn("Encuesta con ID {} no encontrada.", oid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Encuesta no encontrada");
            }
        } catch (Exception e) {
            logger.error("Error al buscar la encuesta con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Encuesta no encontrada");
        }

        try {
            encuestaService.delete(oid);
            logger.info("Encuesta con ID {} eliminada exitosamente.", oid);
        } catch (Exception e) {
            logger.error("Error al eliminar la encuesta con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede borrar por conflictos con otros datos.");
        }
        return ResponseEntity.ok().build();
    }
}
