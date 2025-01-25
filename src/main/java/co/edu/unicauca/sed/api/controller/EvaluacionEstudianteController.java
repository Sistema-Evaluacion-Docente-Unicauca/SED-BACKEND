package co.edu.unicauca.sed.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import co.edu.unicauca.sed.api.model.EvaluacionEstudiante;
import co.edu.unicauca.sed.api.service.EvaluacionEstudianteService;

@Controller
@RequestMapping("api/evaluacion-estudiante")
public class EvaluacionEstudianteController {

    @Autowired
    private EvaluacionEstudianteService evaluacionEstudianteService;

    @GetMapping
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

    @GetMapping("/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        EvaluacionEstudiante resultado = evaluacionEstudianteService.findByOid(oid);
        if (resultado != null) {
            return ResponseEntity.ok().body(resultado);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> saveEvaluacionConEncuesta(@RequestBody EvaluacionEstudiante evaluacionEstudiante,
            @RequestParam Integer oidEncuesta) {
        try {
            EvaluacionEstudiante savedEvaluacion = evaluacionEstudianteService
                    .saveEvaluacionConEncuesta(evaluacionEstudiante, oidEncuesta);
            if (savedEvaluacion != null) {
                return ResponseEntity.ok().body(savedEvaluacion);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        return ResponseEntity.internalServerError().body("Error: Resultado nulo");
    }

    @DeleteMapping("/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        EvaluacionEstudiante evaluacion = null;
        try {
            evaluacion = evaluacionEstudianteService.findByOid(oid);
            if (evaluacion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evaluación de estudiante no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evaluación de estudiante no encontrada");
        }

        try {
            evaluacionEstudianteService.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
