package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import co.edu.unicauca.sed.api.model.PeriodoAcademico;
import co.edu.unicauca.sed.api.service.PeriodoAcademicoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

/**
 * Controlador para la gestión de los períodos académicos.
 * Proporciona endpoints para realizar operaciones CRUD sobre los períodos
 * académicos.
 */
@Controller
@RequestMapping("api/periodos-academicos")
public class PeriodoAcademicoController {

    private static final Logger logger = LoggerFactory.getLogger(PeriodoAcademicoController.class);

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    /**
     * Recupera todos los períodos académicos disponibles con soporte de paginación.
     *
     * @param page Número de página a recuperar (por defecto 0).
     * @param size Cantidad de elementos por página (por defecto 10).
     * @return Una página con los períodos académicos disponibles o un mensaje de error si no se encuentran.
     */
    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<PeriodoAcademico> periodos = periodoAcademicoService.findAll(PageRequest.of(page, size));
            if (periodos.hasContent()) {
                return ResponseEntity.ok().body(periodos);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin períodos académicos disponibles.");
            }
        } catch (Exception e) {
            logger.error("Error al obtener los períodos académicos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Busca un período académico específico por su ID.
     *
     * @param oid El ID del período académico.
     * @return El período académico encontrado o un error 404 si no se encuentra.
     */
    @GetMapping("/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        try {
            PeriodoAcademico resultado = periodoAcademicoService.findByOid(oid);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Período académico con ID " + oid + " no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error al buscar el período académico con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Guarda un nuevo período académico en el sistema.
     *
     * @param periodoAcademico El objeto PeriodoAcademico a guardar.
     * @return El período académico guardado o un mensaje de error.
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestBody PeriodoAcademico periodoAcademico) {
        try {
            PeriodoAcademico resultado = periodoAcademicoService.save(periodoAcademico);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            } else {
                logger.error("Error al guardar el período académico. Resultado nulo.");
                return ResponseEntity.internalServerError().body("Error: Resultado nulo");
            }
        } catch (Exception e) {
            logger.error("Error al guardar el período académico: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Actualiza un período académico existente.
     *
     * @param oid              El ID del período académico a actualizar.
     * @param periodoAcademico Datos actualizados del período académico.
     * @return Mensaje de éxito o error.
     */
    @PutMapping("/{oid}")
    public ResponseEntity<?> update(@PathVariable Integer oid, @RequestBody PeriodoAcademico periodoAcademico) {
        try {
            boolean updated = periodoAcademicoService.update(oid, periodoAcademico);
            if (updated) {
                return ResponseEntity.ok("Período académico actualizado correctamente.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Período académico con ID " + oid + " no encontrado.");
            }
        } catch (Exception e) {
            logger.error("Error al actualizar el período académico con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Elimina un período académico por su ID.
     *
     * @param oid El ID del período académico a eliminar.
     * @return Mensaje de confirmación si se elimina, o un error si ocurre un
     *         problema.
     */
    @DeleteMapping("/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        try {
            PeriodoAcademico periodoAcademico = periodoAcademicoService.findByOid(oid);
            if (periodoAcademico == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Período académico no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error al buscar el período académico con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Período académico no encontrado");
        }

        try {
            periodoAcademicoService.delete(oid);
            logger.info("Período académico con ID {} eliminado exitosamente.", oid);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error al eliminar el período académico con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
    }
}
