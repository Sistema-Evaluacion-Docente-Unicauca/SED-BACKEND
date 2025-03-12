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

import co.edu.unicauca.sed.api.domain.EncuestaEstudiante;
import co.edu.unicauca.sed.api.service.encuesta.EncuestaEstudianteService;

@Controller
@RequestMapping("api/encuesta-estudiante")
public class EncuestaEstudianteController {

    @Autowired
    private EncuestaEstudianteService encuestaEstudianteService;

    @GetMapping
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

    @GetMapping("/{oid}")
    public ResponseEntity<?> find(@PathVariable Integer oid) {
        EncuestaEstudiante encuestaEstudiante = encuestaEstudianteService.findByOid(oid);
        if (encuestaEstudiante != null) {
            return ResponseEntity.ok().body(encuestaEstudiante);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
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

    @DeleteMapping("/{oid}")
    public ResponseEntity<?> delete(@PathVariable Integer oid) {
        EncuestaEstudiante encuestaEstudiante = null;
        try {
            encuestaEstudiante = encuestaEstudianteService.findByOid(oid);
            if (encuestaEstudiante == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EncuestaEstudiante no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EncuestaEstudiante no encontrada");
        }

        try {
            encuestaEstudianteService.delete(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede borrar por conflictos con otros datos");
        }
        
        return ResponseEntity.ok().build();
    }

}
