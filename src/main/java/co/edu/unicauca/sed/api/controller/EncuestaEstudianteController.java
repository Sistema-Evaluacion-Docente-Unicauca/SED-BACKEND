package co.edu.unicauca.sed.api.controller;

import co.edu.unicauca.sed.api.model.EncuestaEstudiante;
import co.edu.unicauca.sed.api.service.EncuestaEstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("encuestaEstudiante")
public class EncuestaEstudianteController {

    @Autowired
    private EncuestaEstudianteService encuestaEstudianteService;

    @GetMapping("all")
    public ResponseEntity<?> findAll() {
        try {
            List<EncuestaEstudiante> list = (List<EncuestaEstudiante>) encuestaEstudianteService.findAll();
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
        EncuestaEstudiante encuestaEstudiante = encuestaEstudianteService.findById(oid).orElse(null);
        if (encuestaEstudiante != null) {
            return ResponseEntity.ok().body(encuestaEstudiante);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody EncuestaEstudiante encuestaEstudiante) {
        try {
            EncuestaEstudiante resultado = encuestaEstudianteService.save(encuestaEstudiante);
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
        EncuestaEstudiante encuestaEstudiante = null;
        try {
            encuestaEstudiante = encuestaEstudianteService.findById(oid).orElse(null);
            if (encuestaEstudiante == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EncuestaEstudiante no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EncuestaEstudiante no encontrada");
        }

        try {
            encuestaEstudianteService.deleteById(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        return ResponseEntity.ok().build();
    }
}
