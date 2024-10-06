package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.Autoevaluacion;
import co.edu.unicauca.sed.api.service.AutoevaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("autoevaluacion")
public class AutoevaluacionController {

    @Autowired
    private AutoevaluacionService service;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Autoevaluacion> list = service.findAll();
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> findById(@PathVariable Integer oid) {
        Autoevaluacion autoevaluacion = service.findByOid(oid);
        if (autoevaluacion != null) {
            return ResponseEntity.ok().body(autoevaluacion);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Autoevaluacion autoevaluacion) {
        try {
            Autoevaluacion resultado = service.save(autoevaluacion);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Autoevaluacion autoevaluacion = null;
        try {
            autoevaluacion = service.findByOid(oid);
            if (autoevaluacion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Autoevaluación no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Autoevaluación no encontrada");
        }

        try {
            service.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
