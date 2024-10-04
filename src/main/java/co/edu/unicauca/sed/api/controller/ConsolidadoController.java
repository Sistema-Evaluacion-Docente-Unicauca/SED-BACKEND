package co.edu.unicauca.sed.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.sed.api.model.Consolidado;
import co.edu.unicauca.sed.api.service.ConsolidadoService;

@Controller
@RequestMapping("consolidado")
public class ConsolidadoController {
    @Autowired
    private ConsolidadoService consolidadoService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Consolidado> list = consolidadoService.findAll();
            return list.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        Consolidado resultado = this.consolidadoService.findByOid(oid);
        return resultado != null ? ResponseEntity.ok().body(resultado) : ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Consolidado consolidado) {
        try {
            Consolidado resultado = consolidadoService.save(consolidado);
            return resultado != null ? ResponseEntity.ok().body(resultado) : ResponseEntity.internalServerError().body("Error: Resultado nulo");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
    }

    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Consolidado consolidado = consolidadoService.findByOid(oid);
        if (consolidado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consolidado no encontrado");
        }
        try {
            this.consolidadoService.delete(oid);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
    }
}
