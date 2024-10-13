package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.EncuestaPregunta;
import co.edu.unicauca.sed.api.service.EncuestaPreguntaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("encuestaPregunta")
public class EncuestaPreguntaController {

    @Autowired
    private EncuestaPreguntaService encuestaPreguntaService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<EncuestaPregunta> list = encuestaPreguntaService.findAll();
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
        EncuestaPregunta encuestaPregunta = encuestaPreguntaService.findByOid(oid);
        if (encuestaPregunta != null) {
            return ResponseEntity.ok().body(encuestaPregunta);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody EncuestaPregunta encuestaPregunta,
            @RequestParam Integer oidEncuesta,
            @RequestParam Integer oidPregunta) {
        try {
            EncuestaPregunta resultado = encuestaPreguntaService.save(encuestaPregunta, oidEncuesta, oidPregunta);
            if (resultado != null) {
                return ResponseEntity.ok().body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    @DeleteMapping("delete/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        EncuestaPregunta encuestaPregunta = encuestaPreguntaService.findByOid(oid);
        if (encuestaPregunta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EncuestaPregunta no encontrada");
        }

        try {
            encuestaPreguntaService.delete(oid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
