package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.model.Consolidado;
import co.edu.unicauca.sed.api.service.ConsolidadoService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

@Controller
@RequestMapping("consolidado")
public class ConsolidadoController {

    private static final Logger logger = LoggerFactory.getLogger(ConsolidadoController.class);

    @Autowired
    private ConsolidadoService consolidadoService;

    /**
     * Recupera todos los consolidados con soporte de paginación y ordenamiento.
     *
     * @param page           Número de página.
     * @param size           Tamaño de página.
     * @param ascendingOrder Orden ascendente o descendente.
     * @return Página de Consolidado o mensaje de error.
     */
    @GetMapping("all")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") Boolean ascendingOrder) {
        try {
            Page<Consolidado> pageResult = consolidadoService.findAll(PageRequest.of(page, size), ascendingOrder);
            if (pageResult.hasContent()) {
                return ResponseEntity.ok().body(pageResult);
            }
        } catch (Exception e) {
            logger.error("Error al obtener la lista de consolidados: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        logger.warn("No se encontraron consolidados en la base de datos.");
        return ResponseEntity.noContent().build();
    }

    /**
     * Recupera un Consolidado por su OID.
     *
     * @param oid El ID del Consolidado.
     * @return El objeto Consolidado o un mensaje de error.
     */
    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        try {
            Consolidado resultado = this.consolidadoService.findByOid(oid);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
            logger.warn("Consolidado con ID {} no encontrado.", oid);
        } catch (Exception e) {
            logger.error("Error al buscar el consolidado con ID {}: {}", oid, e.getMessage(), e);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Guarda un nuevo Consolidado.
     *
     * @param consolidado El objeto Consolidado a guardar.
     * @return El objeto Consolidado guardado o un mensaje de error.
     */
    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Consolidado consolidado) {
        try {
            Consolidado resultado = consolidadoService.save(consolidado);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            logger.error("Error al guardar el consolidado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        logger.error("El resultado del guardado fue nulo. Verifique los datos de entrada.");
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    /**
     * Elimina un Consolidado por su OID.
     *
     * @param oid El ID del Consolidado a eliminar.
     * @return Mensaje de éxito o error.
     */
    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        try {
            Consolidado consolidado = consolidadoService.findByOid(oid);
            if (consolidado == null) {
                logger.warn("No se pudo eliminar. Consolidado con ID {} no encontrado.", oid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consolidado no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error al buscar el consolidado con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consolidado no encontrado");
        }

        try {
            this.consolidadoService.delete(oid);
        } catch (Exception e) {
            logger.error("Error al eliminar el consolidado con ID {}: {}", oid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        logger.info("Consolidado con ID {} eliminado exitosamente.", oid);
        return ResponseEntity.ok().build();
    }

    /**
     * Genera un consolidado para un evaluado en un período académico.
     *
     * @param evaluadoId       ID del evaluado.
     * @param periodoAcademico (Opcional) ID del período académico.
     * @return Consolidado generado o un mensaje de error.
     */
    @GetMapping("/generarConsolidado")
    public ResponseEntity<Object> generarConsolidado(
            @RequestParam Integer idEvaluado,
            @RequestParam(required = false) Integer periodoAcademico) {
        try {
            ConsolidadoDTO consolidado = consolidadoService.generarConsolidado(idEvaluado, periodoAcademico);
            return ResponseEntity.ok(consolidado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al generar el consolidado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al generar el consolidado para el evaluado con ID {}: {}", idEvaluado, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Aprueba un consolidado, lo guarda en la base de datos y genera un archivo Excel.
     *
     * @param idEvaluado         ID del evaluado.
     * @param idPeriodoAcademico (Opcional) ID del período académico.
     * @param nota               (Opcional) Nota asociada al consolidado.
     * @return Mensaje de éxito o error.
     */
    @PostMapping("/aprobarConsolidado")
    public ResponseEntity<?> aprobarConsolidado(
            @RequestParam Integer idEvaluado,
            @RequestParam(required = false) Integer idPeriodoAcademico,
            @RequestParam Integer idEvaluador,
            @RequestParam(required = false) String nota) {
        try {
            consolidadoService.aprobarConsolidado(idEvaluado, idEvaluador, idPeriodoAcademico, nota);
            return ResponseEntity.ok("Consolidado aprobado y archivo generado correctamente.");
        } catch (Exception e) {
            logger.error("Error al aprobar el consolidado para el evaluado con ID {}: {}", idEvaluado, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al aprobar el consolidado: " + e.getMessage());
        }
    }

    /**
     * Descarga el archivo consolidado por su ID.
     *
     * @param idConsolidado ID del consolidado a descargar.
     * @return Archivo consolidado como recurso.
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(
            @PathVariable("id") Integer id) {
        logger.info("Solicitud recibida para descargar archivo de la fuente con ID {} con bandera de informe {}", id);
        return consolidadoService.getFile(id);
    }
}
