package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.Encuesta;
import co.edu.unicauca.sed.api.service.EncuestaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("encuesta")
public class EncuestaController {

    @Autowired
    private EncuestaService encuestaService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<Encuesta> list = (List<Encuesta>) encuestaService.findAll();
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
        Encuesta encuesta = encuestaService.findById(oid).orElse(null);
        if (encuesta != null) {
            return ResponseEntity.ok().body(encuesta);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Encuesta encuesta) {
        try {
            Encuesta resultado = encuestaService.save(encuesta);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> deleteById(@PathVariable Integer oid) {
        Encuesta encuesta = null;
        try {
            encuesta = encuestaService.findById(oid).orElse(null);
            if (encuesta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Encuesta no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Encuesta no encontrada");
        }

        try {
            encuestaService.deleteById(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
