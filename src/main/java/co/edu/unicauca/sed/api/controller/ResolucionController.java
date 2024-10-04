package co.edu.unicauca.sed.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.sed.api.model.Resolucion;
import co.edu.unicauca.sed.api.service.ResolucionService;

@Controller
@RequestMapping("resolucion")
public class ResolucionController {
    @Autowired
    private ResolucionService resolucionService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Resolucion> list = resolucionService.findAll();
            return list.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        Resolucion resultado = this.resolucionService.findByOid(oid);
        return resultado != null ? ResponseEntity.ok().body(resultado) : ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Resolucion resolucion) {
        try {
            Resolucion resultado = resolucionService.save(resolucion);
            return resultado != null ? ResponseEntity.ok().body(resultado) : ResponseEntity.internalServerError().body("Error: Resultado nulo");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
    }

    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Resolucion resolucion = resolucionService.findByOid(oid);
        if (resolucion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resolución no encontrada");
        }
        try {
            this.resolucionService.delete(oid);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
    }
}
