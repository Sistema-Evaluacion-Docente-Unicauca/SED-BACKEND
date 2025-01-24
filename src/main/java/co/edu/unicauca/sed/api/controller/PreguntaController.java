package co.edu.unicauca.sed.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.sed.api.model.Pregunta;
import co.edu.unicauca.sed.api.service.PreguntaService;

@Controller
@RequestMapping("api/pregunta")
public class PreguntaController {

    @Autowired
    private PreguntaService preguntaService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<Pregunta> list = preguntaService.findAll();
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{oid}")
    public ResponseEntity<?> findById(@PathVariable Integer oid) {
        Pregunta pregunta = preguntaService.findByOid(oid);
        if (pregunta != null) {
            return ResponseEntity.ok().body(pregunta);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Pregunta pregunta) {
        try {
            Pregunta savedPregunta = preguntaService.save(pregunta);
            if (savedPregunta != null) {
                return ResponseEntity.ok(savedPregunta);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    // Nuevo método para guardar múltiples preguntas
    @PostMapping("saveAll")
    public ResponseEntity<?> saveAll(@RequestBody List<Pregunta> preguntas) {
        try {
            List<Pregunta> savedPreguntas = preguntaService.saveAll(preguntas);
            if (savedPreguntas != null && !savedPreguntas.isEmpty()) {
                return ResponseEntity.ok(savedPreguntas);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.internalServerError().body("Error: No se pudieron guardar las preguntas");
    }

    @DeleteMapping("/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        Pregunta pregunta = preguntaService.findByOid(oid);
        if (pregunta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pregunta no encontrada");
        }

        try {
            preguntaService.delete(oid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
