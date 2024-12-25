package co.edu.unicauca.sed.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("consolidado")
public class ConsolidadoController {

    private static final Logger logger = LoggerFactory.getLogger(ConsolidadoController.class);
    
    @Autowired
    private ConsolidadoService consolidadoService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Consolidado> list = consolidadoService.findAll();
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        Consolidado resultado = this.consolidadoService.findByOid(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Consolidado consolidado) {
        try {
            Consolidado resultado = consolidadoService.save(consolidado);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Consolidado consolidado = null;
        try {
            consolidado = consolidadoService.findByOid(oid);
            if (consolidado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consolidado no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consolidado no encontrado");
        }

        try {
            this.consolidadoService.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Genera un consolidado para un evaluado en un período académico.
     *
     * @param evaluadoId       ID del evaluado para generar el consolidado.
     * @param periodoAcademico ID del período académico (opcional).
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al generar el consolidado para el evaluado con ID {}: {}", idEvaluado, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error",  e.getMessage()));
        }
    }

    /**
     * Aprueba el consolidado, lo guarda en la base de datos y genera un archivo Excel.
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
            @RequestParam(required = false) String nota) {
        try {
            consolidadoService.aprobarConsolidado(idEvaluado, idPeriodoAcademico, nota);
            return ResponseEntity.ok("Consolidado aprobado y archivo generado correctamente.");
        } catch (Exception e) {
            logger.error("Error al aprobar el consolidado para el evaluado con ID {}: {}", idEvaluado, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al aprobar el consolidado: " + e.getMessage());
        }
    }
}
