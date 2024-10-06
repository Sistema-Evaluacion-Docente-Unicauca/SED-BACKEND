package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.EvaluacionEstudiante;
import co.edu.unicauca.sed.api.service.EvaluacionEstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("evaluacionEstudiante")
public class EvaluacionEstudianteController {

    @Autowired
    private EvaluacionEstudianteService evaluacionEstudianteService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<EvaluacionEstudiante> list = (List<EvaluacionEstudiante>) evaluacionEstudianteService.findAll();
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok().body(list);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getStackTrace());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("find/{oid}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        EvaluacionEstudiante evaluacion = evaluacionEstudianteService.findById(id).orElse(null);
        if (evaluacion != null) {
            return ResponseEntity.ok().body(evaluacion);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> saveEvaluacionConEncuesta(@RequestBody EvaluacionEstudiante evaluacionEstudiante,
            @RequestParam Integer oidEncuesta) {
        try {
            EvaluacionEstudiante savedEvaluacion = evaluacionEstudianteService
                    .saveEvaluacionConEncuesta(evaluacionEstudiante, oidEncuesta);
            return ResponseEntity.ok(savedEvaluacion);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        EvaluacionEstudiante evaluacion = null;
        try {
            evaluacion = evaluacionEstudianteService.findById(id).orElse(null);
            if (evaluacion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evaluación de estudiante no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evaluación de estudiante no encontrada");
        }

        try {
            evaluacionEstudianteService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
