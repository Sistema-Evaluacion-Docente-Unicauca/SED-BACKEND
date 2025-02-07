package co.edu.unicauca.sed.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import co.edu.unicauca.sed.api.dto.ConsolidadoDTO;
import co.edu.unicauca.sed.api.dto.actividad.ActividadPaginadaDTO;
import co.edu.unicauca.sed.api.model.Consolidado;
import co.edu.unicauca.sed.api.service.ConsolidadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Controller
@RequestMapping("api/consolidado")
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
    @GetMapping
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

    @GetMapping("/{oid}")
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

    @PostMapping
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

    @PutMapping("/{oidConsolidado}")
    public ResponseEntity<?> update(@PathVariable Integer oidConsolidado, @RequestBody Consolidado consolidado) {
        try {
            consolidadoService.updateAllFromConsolidado(oidConsolidado, consolidado);
            return ResponseEntity.ok("Consolidados actualizados correctamente.");
        } catch (IllegalArgumentException e) {
            logger.warn("Actualización fallida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error al actualizar los consolidados: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar los consolidados: " + e.getMessage());
        }
    }

    @DeleteMapping("/{oid}")
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
     * Endpoint para obtener solo la información general del consolidado sin actividades.
     */
    @GetMapping("/informacion-general")
    public ResponseEntity<ConsolidadoDTO> obtenerInformacionGeneral(
            @RequestParam Integer idEvaluado,
            @RequestParam(required = false) Integer periodoAcademico) {
        try {
            ConsolidadoDTO consolidado = consolidadoService.generarInformacionGeneral(idEvaluado, periodoAcademico);
            return ResponseEntity.ok(consolidado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al obtener la información general: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error inesperado en obtenerInformaciónGeneral: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint para obtener solo las actividades del consolidado paginadas.
     */
    @GetMapping("/actividades")
    public ResponseEntity<ActividadPaginadaDTO> obtenerActividadesPaginadas(
            @RequestParam Integer idEvaluado,
            @RequestParam(required = false) Integer periodoAcademico,
            Pageable pageable) {
        try {
            ActividadPaginadaDTO actividades = consolidadoService.obtenerActividadesPaginadas(idEvaluado, periodoAcademico, pageable);
            return ResponseEntity.ok(actividades);
        } catch (Exception e) {
            logger.error("Error al obtener actividades paginadas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint para aprobar el consolidado y generar el Excel.
     */
    @PostMapping("/aprobar")
    public ResponseEntity<String> aprobarConsolidado(
            @RequestParam Integer idEvaluado,
            @RequestParam(required = false) Integer periodoAcademico,
            @RequestParam(required = false) String nota) {
        try {
            consolidadoService.aprobarConsolidado(idEvaluado, periodoAcademico, nota);
            return ResponseEntity.ok("Consolidado aprobado y archivo generado correctamente.");
        } catch (Exception e) {
            logger.error("Error al aprobar el consolidado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
