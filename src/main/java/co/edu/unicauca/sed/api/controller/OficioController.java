package co.edu.unicauca.sed.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.sed.api.model.Oficio;
import co.edu.unicauca.sed.api.service.OficioService;

@Controller
@RequestMapping("oficio")
public class OficioController {
    @Autowired
    private OficioService oficioService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Oficio> list = oficioService.findAll();
            return list.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        Oficio resultado = this.oficioService.findByOid(oid);
        return resultado != null ? ResponseEntity.ok().body(resultado) : ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Oficio oficio) {
        try {
            Oficio resultado = oficioService.save(oficio);
            return resultado != null ? ResponseEntity.ok().body(resultado) : ResponseEntity.internalServerError().body("Error: Resultado nulo");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error:" + e.getStackTrace());
        }
    }

    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Oficio oficio = oficioService.findByOid(oid);
        if (oficio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Oficio no encontrado");
        }
        try {
            this.oficioService.delete(oid);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
    }
}
