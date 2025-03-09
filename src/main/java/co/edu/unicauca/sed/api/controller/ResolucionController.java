package co.edu.unicauca.sed.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.sed.api.domain.Resolucion;
import co.edu.unicauca.sed.api.service.ResolucionService;

@Controller
@RequestMapping("api/resolucion")
public class ResolucionController {
    @Autowired
    private ResolucionService resolucionService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<Resolucion> list = resolucionService.findAll();
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        Resolucion resolucion = this.resolucionService.findByOid(oid);
        if (resolucion != null) {
            return ResponseEntity.ok().body(resolucion);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Resolucion resolucion) {
        try {
            Resolucion resultado = resolucionService.save(resolucion);

            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    @DeleteMapping("/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Resolucion resolucion = null;
        try {
            resolucion = this.resolucionService.findByOid(oid);
            if (resolucion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resolución no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resolución no encontrada");
        }

        try {
            this.resolucionService.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
