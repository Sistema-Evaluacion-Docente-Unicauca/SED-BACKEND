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

@Controller
@RequestMapping("consolidado")
public class ConsolidadoController {
    
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
            @RequestParam Integer evaluadoId,
            @RequestParam(required = false) Integer periodoAcademico) {
        try {
            ConsolidadoDTO consolidado = consolidadoService.generarConsolidado(evaluadoId, periodoAcademico);
            return ResponseEntity.ok(consolidado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error",  e.getMessage()));
        }
    }
}
